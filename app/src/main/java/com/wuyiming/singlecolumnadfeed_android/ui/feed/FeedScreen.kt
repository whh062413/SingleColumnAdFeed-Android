package com.wuyiming.singlecolumnadfeed_android.ui.feed

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListItemInfo
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.wuyiming.singlecolumnadfeed_android.data.model.FeedCategory
import com.wuyiming.singlecolumnadfeed_android.data.model.FeedItem
import com.wuyiming.singlecolumnadfeed_android.data.model.FeedItemType
import com.wuyiming.singlecolumnadfeed_android.ui.components.AdCardFactory
import com.wuyiming.singlecolumnadfeed_android.ui.components.FeedEmptyState
import com.wuyiming.singlecolumnadfeed_android.ui.components.FeedErrorState
import com.wuyiming.singlecolumnadfeed_android.ui.components.FeedSkeletonList
import com.wuyiming.singlecolumnadfeed_android.ui.theme.AdFeedTheme
import com.wuyiming.singlecolumnadfeed_android.viewmodel.FeedScreenState
import com.wuyiming.singlecolumnadfeed_android.viewmodel.FeedViewModel
import com.wuyiming.singlecolumnadfeed_android.viewmodel.LoadMoreState
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedScreen(
    onItemClick: (FeedItem) -> Unit,
    viewModel: FeedViewModel = FeedViewModel.getSharedInstance(
        LocalContext.current.applicationContext as android.app.Application
    )
) {
    val items by viewModel.feedItems.collectAsState()
    val currentCategory by viewModel.currentCategory.collectAsState()
    val refreshing by viewModel.refreshing.collectAsState()
    val loadingMore by viewModel.loadingMore.collectAsState()
    val hasMore by viewModel.hasMore.collectAsState()
    val loadMoreState by viewModel.currentLoadMoreState.collectAsState()
    val screenState by viewModel.currentScreenState.collectAsState()
    val isSearchMode by viewModel.searchMode.collectAsState()
    val isSearching by viewModel.searching.collectAsState()
    var searchQuery by rememberSaveable(stateSaver = TextFieldValue.Saver) { mutableStateOf(TextFieldValue("")) }
    val focusManager = LocalFocusManager.current

    val listState = rememberLazyListState()
    val autoPlayVideoId by rememberAutoPlayVideoId(listState, items)
    val preloadVideoIds by rememberPreloadVideoIds(listState, items)

    LaunchedEffect(listState) {
        if (!isSearchMode) {
            snapshotFlow {
                val layoutInfo = listState.layoutInfo
                val totalItems = layoutInfo.totalItemsCount
                val lastVisibleItem = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
                lastVisibleItem >= totalItems - 3 && totalItems > 0
            }.distinctUntilChanged().collect { shouldLoadMore ->
                if (shouldLoadMore && !loadingMore && hasMore) viewModel.loadMore()
            }
        }
    }

    AdFeedTheme {
        Scaffold(
            topBar = {
                Column {
                    CenterAlignedTopAppBar(title = { Text("广告信息流") })
                    CategoryTabs(currentCategory) { viewModel.switchCategory(it) }
                    SearchBar(
                        query = searchQuery, onQueryChange = { searchQuery = it },
                        onSearch = { focusManager.clearFocus(); viewModel.search(it) },
                        onClear = { searchQuery = TextFieldValue(""); viewModel.clearSearch(); focusManager.clearFocus() }
                    )
                }
            }
        ) { padding ->
            PullToRefreshBox(
                isRefreshing = refreshing, onRefresh = { viewModel.refresh() },
                modifier = Modifier.padding(padding)
            ) {
                when (val state = screenState) {
                    is FeedScreenState.Loading -> FeedSkeletonList()
                    is FeedScreenState.Error -> FeedErrorState(state.message, onRetry = { viewModel.refresh() })
                    is FeedScreenState.Empty -> FeedEmptyState()
                    is FeedScreenState.Content -> {
                        LazyColumn(state = listState) {
                            if (isSearchMode) {
                                item {
                                    Row(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                        Text(if (isSearching) "搜索中..." else "搜索结果: ${items.size}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                                    }
                                }
                            }
                            items(items, key = { it.id }, contentType = { it.type }) { item ->
                                AdCardFactory(
                                    item = item,
                                    onLikeClick = { viewModel.toggleLike(it) },
                                    onCollectClick = { viewModel.toggleCollect(it) },
                                    onCommentClick = { onItemClick(item) },
                                    onCardClick = { onItemClick(item) },
                                    shouldAutoPlayVideo = autoPlayVideoId == item.id,
                                    shouldPreloadVideo = preloadVideoIds.contains(item.id)
                                )
                            }
                            if (!isSearchMode && loadingMore) {
                                item {
                                    Box(Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                                            Spacer(Modifier.padding(start = 10.dp))
                                            Text("正在加载更多...", style = MaterialTheme.typography.bodyMedium)
                                        }
                                    }
                                }
                            }
                            if (!isSearchMode && !hasMore && items.isNotEmpty()) {
                                item {
                                    Box(Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                                        Text("没有更多了", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryTabs(currentCategory: FeedCategory, onCategorySelected: (FeedCategory) -> Unit) {
    val categories = listOf(FeedCategory.RECOMMEND, FeedCategory.ECOMMERCE, FeedCategory.LOCAL)
    TabRow(selectedTabIndex = categories.indexOf(currentCategory)) {
        categories.forEach { category ->
            Tab(selected = currentCategory == category, onClick = { onCategorySelected(category) },
                text = { Text(category.title) })
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(
    query: TextFieldValue, onQueryChange: (TextFieldValue) -> Unit,
    onSearch: (String) -> Unit, onClear: () -> Unit
) {
    OutlinedTextField(
        value = query, onValueChange = onQueryChange,
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
        placeholder = { Text("搜索广告内容…") },
        leadingIcon = { Icon(Icons.Default.Search, "搜索") },
        trailingIcon = {
            if (query.text.isNotEmpty()) {
                IconButton(onClick = onClear) { Icon(Icons.Default.Close, "清除") }
            }
        },
        singleLine = true, shape = MaterialTheme.shapes.medium,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
        ),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = { onSearch(query.text) })
    )
}

@Composable
private fun rememberAutoPlayVideoId(
    listState: LazyListState, items: List<FeedItem>
): androidx.compose.runtime.State<String?> {
    val videoIds = remember(items) { items.filter { it.type == FeedItemType.VIDEO }.map { it.id }.toSet() }
    return snapshotFlow {
        listState.layoutInfo.visibleItemsInfo
            .mapNotNull { itemInfo ->
                val id = itemInfo.key as? String ?: return@mapNotNull null
                if (!videoIds.contains(id)) return@mapNotNull null
                id to itemVisibleRatio(itemInfo, listState)
            }
            .filter { (_, ratio) -> ratio >= 0.6f }
            .maxByOrNull { (_, ratio) -> ratio }?.first
    }.collectAsState(initial = null)
}

@Composable
private fun rememberPreloadVideoIds(
    listState: LazyListState, items: List<FeedItem>
): androidx.compose.runtime.State<Set<String>> {
    val videoIds = remember(items) { items.filter { it.type == FeedItemType.VIDEO }.map { it.id }.toSet() }
    return snapshotFlow {
        listState.layoutInfo.visibleItemsInfo
            .mapNotNull { it.key as? String }
            .filter { videoIds.contains(it) }.toSet()
    }.collectAsState(initial = emptySet())
}

private fun itemVisibleRatio(itemInfo: LazyListItemInfo, listState: LazyListState): Float {
    val itemStart = itemInfo.offset
    val itemEnd = itemInfo.offset + itemInfo.size
    val viewportStart = listState.layoutInfo.viewportStartOffset
    val viewportEnd = listState.layoutInfo.viewportEndOffset
    val visibleStart = maxOf(itemStart, viewportStart)
    val visibleEnd = minOf(itemEnd, viewportEnd)
    val visibleSize = (visibleEnd - visibleStart).coerceAtLeast(0)
    return visibleSize.toFloat() / itemInfo.size.toFloat()
}
