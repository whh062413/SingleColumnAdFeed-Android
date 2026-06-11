package com.wuyiming.singlecolumnadfeed_android.ui.feed

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.wuyiming.singlecolumnadfeed_android.data.model.FeedCategory
import com.wuyiming.singlecolumnadfeed_android.data.model.FeedItem
import com.wuyiming.singlecolumnadfeed_android.ui.components.AdCardFactory
import com.wuyiming.singlecolumnadfeed_android.ui.components.SkeletonLoading
import com.wuyiming.singlecolumnadfeed_android.ui.theme.AdFeedTheme
import com.wuyiming.singlecolumnadfeed_android.viewmodel.FeedViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedScreen(
    onItemClick: (FeedItem) -> Unit,
    viewModel: FeedViewModel = FeedViewModel.getSharedInstance(
        LocalContext.current.applicationContext as android.app.Application
    )
) {
    val uiState by viewModel.uiState.observeAsState()
    val listState = rememberLazyListState()
    var searchQuery by remember { mutableStateOf(TextFieldValue("")) }
    val focusManager = LocalFocusManager.current
    val coroutineScope = rememberCoroutineScope()

    // Track previous refresh state to detect when refresh completes
    var wasRefreshing by remember { mutableStateOf(false) }

    LaunchedEffect(uiState?.isRefreshing) {
        val refreshing = uiState?.isRefreshing ?: false
        if (wasRefreshing && !refreshing && listState.firstVisibleItemIndex > 0) {
            // Refresh just completed — scroll to top
            listState.animateScrollToItem(0)
        }
        wasRefreshing = refreshing
    }

    AdFeedTheme {
        Scaffold(
            topBar = {
                Column {
                    CenterAlignedTopAppBar(
                        title = { Text("广告信息流") }
                    )
                    CategoryTabs(
                        currentCategory = uiState?.currentCategory ?: FeedCategory.RECOMMEND,
                        onCategorySelected = { viewModel.switchCategory(it) }
                    )
                    SearchBar(
                        query = searchQuery,
                        onQueryChange = { searchQuery = it },
                        onSearch = {
                            focusManager.clearFocus()
                            viewModel.search(it)
                        },
                        onClear = {
                            searchQuery = TextFieldValue("")
                            viewModel.clearSearch()
                            focusManager.clearFocus()
                        }
                    )
                }
            }
        ) { padding ->
            PullToRefreshBox(
                isRefreshing = uiState?.isRefreshing ?: false,
                onRefresh = { viewModel.refresh() },
                modifier = Modifier.padding(padding)
            ) {
                val items = uiState?.items ?: emptyList()
                val isLoading = uiState?.isLoading ?: false
                val isSearching = uiState?.isSearching ?: false
                val isSearchMode = uiState?.isSearchMode ?: false

                if (isLoading && items.isEmpty()) {
                    LazyColumn {
                        items(5) { SkeletonLoading() }
                    }
                } else {
                    LazyColumn(state = listState) {
                        if (isSearchMode && !isSearching) {
                            item {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "搜索到 ${items.size} 条结果",
                                        fontSize = 14.sp,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                    )
                                    TextButton(onClick = {
                                        searchQuery = TextFieldValue("")
                                        viewModel.clearSearch()
                                    }) {
                                        Text("清除")
                                    }
                                }
                            }
                        }

                        if (isSearching) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(32.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator()
                                }
                            }
                        }

                        items(items, key = { it.id }) { item ->
                            AdCardFactory(
                                item = item,
                                onCardClick = { onItemClick(item) },
                                onLikeClick = { viewModel.toggleLike(item.id) },
                                onCommentClick = { onItemClick(item) }
                            )
                        }

                        if (!isSearchMode && uiState?.isLoadingMore == true) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator()
                                }
                            }
                        }

                        if (!isSearchMode && uiState?.hasMore() == false && items.isNotEmpty()) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(24.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "没有更多了",
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            if (uiState?.isSearchMode != true) {
                LaunchedEffect(listState) {
                    snapshotFlow {
                        val layoutInfo = listState.layoutInfo
                        val totalItems = layoutInfo.totalItemsCount
                        val lastVisibleItem = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
                        lastVisibleItem >= totalItems - 3 && totalItems > 0
                    }.collect { shouldLoadMore ->
                        if (shouldLoadMore && uiState?.isLoadingMore != true && uiState?.hasMore() == true) {
                            viewModel.loadMore()
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryTabs(
    currentCategory: FeedCategory,
    onCategorySelected: (FeedCategory) -> Unit
) {
    val categories = listOf(
        FeedCategory.RECOMMEND to "推荐",
        FeedCategory.ECOMMERCE to "电商",
        FeedCategory.LOCAL to "本地"
    )

    TabRow(
        selectedTabIndex = categories.indexOfFirst { it.first == currentCategory }
    ) {
        categories.forEach { (category, label) ->
            Tab(
                selected = currentCategory == category,
                onClick = { onCategorySelected(category) },
                text = { Text(label) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(
    query: TextFieldValue,
    onQueryChange: (TextFieldValue) -> Unit,
    onSearch: (String) -> Unit,
    onClear: () -> Unit
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        placeholder = { Text("搜索广告内容…") },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "搜索"
            )
        },
        trailingIcon = {
            if (query.text.isNotEmpty()) {
                IconButton(onClick = onClear) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "清除"
                    )
                }
            }
        },
        singleLine = true,
        shape = MaterialTheme.shapes.medium,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
        ),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(
            onSearch = { onSearch(query.text) }
        )
    )
}