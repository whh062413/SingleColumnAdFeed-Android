package com.wuyiming.singlecolumnadfeed_android.ui.feed

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

import com.wuyiming.singlecolumnadfeed_android.data.model.FeedCategory
import com.wuyiming.singlecolumnadfeed_android.data.model.FeedItem
import com.wuyiming.singlecolumnadfeed_android.ui.components.AdCardFactory
import com.wuyiming.singlecolumnadfeed_android.ui.components.SkeletonLoading
import com.wuyiming.singlecolumnadfeed_android.ui.theme.AdFeedTheme
import com.wuyiming.singlecolumnadfeed_android.viewmodel.FeedViewModel

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

                if (isLoading && items.isEmpty()) {
                    // Skeleton loading
                    LazyColumn {
                        items(5) {
                            SkeletonLoading()
                        }
                    }
                } else {
                    LazyColumn(state = listState) {
                        items(items, key = { it.id }) { item ->
                            AdCardFactory(
                                item = item,
                                onCardClick = { onItemClick(item) },
                                onLikeClick = { viewModel.toggleLike(item.id) },
                                onCommentClick = { onItemClick(item) }
                            )
                        }

                        // Load more
                        if (uiState?.isLoadingMore == true) {
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

                        // End indicator
                        if (uiState?.hasMore() == false && items.isNotEmpty()) {
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

            // Detect scroll to bottom for load more
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

@Composable
fun CategoryTabs(
    currentCategory: FeedCategory,
    onCategorySelected: (FeedCategory) -> Unit
) {
    val categories = listOf(
        FeedCategory.RECOMMEND to "精选",
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