package com.wuyiming.singlecolumnadfeed_android.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.wuyiming.singlecolumnadfeed_android.ai.AiInsightGenerator
import com.wuyiming.singlecolumnadfeed_android.ai.AiSearchService
import com.wuyiming.singlecolumnadfeed_android.data.model.FeedCategory
import com.wuyiming.singlecolumnadfeed_android.data.model.FeedItem
import com.wuyiming.singlecolumnadfeed_android.data.repository.DefaultFeedRepository
import com.wuyiming.singlecolumnadfeed_android.data.repository.FeedRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class FeedViewModel(
    application: Application,
    private val feedRepository: DefaultFeedRepository = DefaultFeedRepository(application)
) : AndroidViewModel(application) {

    companion object {
        private const val PAGE_SIZE = 20
        private const val MIN_REFRESH_ANIMATION_MS = 600L

        @Volatile
        private var sharedInstance: FeedViewModel? = null

        @JvmStatic fun getSharedInstance(app: Application): FeedViewModel {
            return sharedInstance ?: synchronized(FeedViewModel::class.java) {
                sharedInstance ?: FeedViewModel(app).also { sharedInstance = it }
            }
        }

        @JvmStatic fun getInstance(app: Application): FeedViewModel = getSharedInstance(app)
    }

    private val allItems = MutableStateFlow<List<FeedItem>>(emptyList())
    private val selectedCategory = MutableStateFlow(FeedCategory.RECOMMEND)
    private val isRefreshing = MutableStateFlow(false)
    private val isLoadingMore = MutableStateFlow(false)
    private val hasMoreItems = MutableStateFlow(true)
    private val loadMoreState = MutableStateFlow<LoadMoreState>(LoadMoreState.Idle)
    private val currentPage = MutableStateFlow(1)
    private val screenState = MutableStateFlow<FeedScreenState>(FeedScreenState.Loading)
    private val isSearchMode = MutableStateFlow(false)
    private val isSearching = MutableStateFlow(false)
    private var refreshSeed = 0
    private var hasLoadedOnce = false
    private var aiInsightJob: Job? = null

    private val _feedItems = MutableStateFlow<List<FeedItem>>(emptyList())
    val allFeedItems: StateFlow<List<FeedItem>> = allItems.asStateFlow()
    val feedItems: StateFlow<List<FeedItem>> = _feedItems.asStateFlow()
    val currentCategory: StateFlow<FeedCategory> = selectedCategory.asStateFlow()
    val refreshing: StateFlow<Boolean> = isRefreshing.asStateFlow()
    val loadingMore: StateFlow<Boolean> = isLoadingMore.asStateFlow()
    val hasMore: StateFlow<Boolean> = hasMoreItems.asStateFlow()
    val currentLoadMoreState: StateFlow<LoadMoreState> = loadMoreState.asStateFlow()
    val currentScreenState: StateFlow<FeedScreenState> = screenState.asStateFlow()
    val searchMode: StateFlow<Boolean> = isSearchMode.asStateFlow()
    val searching: StateFlow<Boolean> = isSearching.asStateFlow()

    // AI services (nullable = AI disabled when not configured)
    private var aiSearchService: AiSearchService? = null

    init {
        observeFilteredItems()
        refresh()
    }

    fun configureAi(apiKey: String, apiUrl: String, model: String) {
        if (apiKey.isBlank()) return
        feedRepository.aiInsightGenerator = AiInsightGenerator(apiKey, apiUrl, model)
        aiSearchService = AiSearchService(apiKey, apiUrl, model)
        // 对已有数据补生成 AI 摘要
        val snapshot = allItems.value
        if (snapshot.isNotEmpty()) generateInsightsFor(snapshot)
    }

    private fun observeFilteredItems() {
        viewModelScope.launch {
            combine(allItems, selectedCategory) { items, category ->
                items.filter { it.category == category }
            }.collect { filteredItems ->
                _feedItems.value = filteredItems
                if (hasLoadedOnce && screenState.value !is FeedScreenState.Error && screenState.value !is FeedScreenState.Loading) {
                    screenState.value = filteredItems.toScreenState()
                }
            }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            val refreshStart = System.currentTimeMillis()
            val showFullLoading = !hasLoadedOnce || screenState.value is FeedScreenState.Error
            if (showFullLoading) screenState.value = FeedScreenState.Loading
            isRefreshing.value = true
            try {
                aiInsightJob?.cancel()
                refreshSeed += 1
                currentPage.value = 1
                hasMoreItems.value = true
                loadMoreState.value = LoadMoreState.Idle
                val loadedItems = feedRepository.loadFeedItems(1, PAGE_SIZE, refreshSeed)
                allItems.value = loadedItems
                hasLoadedOnce = true
                hasMoreItems.value = loadedItems.size == PAGE_SIZE
                screenState.value = loadedItems.toScreenState()
                generateInsightsFor(loadedItems)
            } catch (e: Exception) {
                if (!hasLoadedOnce) {
                    screenState.value = FeedScreenState.Error(e.message ?: "加载失败")
                }
            } finally {
                val elapsed = System.currentTimeMillis() - refreshStart
                val remaining = MIN_REFRESH_ANIMATION_MS - elapsed
                if (remaining > 0) delay(remaining)
                isRefreshing.value = false
            }
        }
    }

    fun loadMore() {
        if (isLoadingMore.value || !hasMoreItems.value || isSearchMode.value) return
        viewModelScope.launch {
            isLoadingMore.value = true
            loadMoreState.value = LoadMoreState.Loading
            try {
                val nextPage = currentPage.value + 1
                val nextItems = feedRepository.loadFeedItems(nextPage, PAGE_SIZE, refreshSeed)
                allItems.value = allItems.value + nextItems
                currentPage.value = nextPage
                hasMoreItems.value = nextItems.isNotEmpty() && nextPage < 5
                loadMoreState.value = if (hasMoreItems.value) LoadMoreState.Idle else LoadMoreState.EndReached
                generateInsightsFor(nextItems)
            } catch (e: Exception) {
                loadMoreState.value = LoadMoreState.Error(e.message ?: "下一页加载失败")
            } finally {
                isLoadingMore.value = false
            }
        }
    }

    fun switchCategory(category: FeedCategory) {
        selectedCategory.value = category
    }

    fun toggleLike(id: String) {
        allItems.value = allItems.value.map { item ->
            if (item.id == id) feedRepository.toggleLike(item) else item
        }
    }

    fun toggleCollect(id: String) {
        allItems.value = allItems.value.map { item ->
            if (item.id == id) feedRepository.toggleCollect(item) else item
        }
    }

    fun search(query: String) {
        if (query.isBlank()) return
        viewModelScope.launch {
            isSearching.value = true
            isSearchMode.value = true
            val trimmed = query.trim()
            // AI 云端拆词优先，失败降级本地分词
            val keywords = try {
                val service = aiSearchService
                if (service != null && trimmed.length > 2) {
                    service.extractKeywords(trimmed)
                } else {
                    splitLocal(trimmed)
                }
            } catch (_: Exception) {
                splitLocal(trimmed)
            }
            val results = allItems.value.filter { item ->
                keywords.any { kw ->
                    item.title.contains(kw, ignoreCase = true) ||
                    item.description.contains(kw, ignoreCase = true) ||
                    item.insight?.tags?.any { it.contains(kw, ignoreCase = true) } == true
                }
            }
            _feedItems.value = results
            isSearching.value = false
            screenState.value = results.toScreenState()
        }
    }

    fun clearSearch() {
        isSearchMode.value = false
        isSearching.value = false
        _feedItems.value = allItems.value.filter { it.category == selectedCategory.value }
        screenState.value = _feedItems.value.toScreenState()
    }

    // ---- AI insight generation (background, non-blocking) ----

    private fun generateInsightsFor(items: List<FeedItem>) {
        val gen = feedRepository.aiInsightGenerator ?: return
        aiInsightJob = viewModelScope.launch {
            for (item in items) {
                // 跳过已有 summary 的项
                if (item.insight?.summary != null && item.insight.summary.isNotEmpty()) continue
                try {
                    val insight = gen.generate(item.title, item.description)
                    if (insight != null) {
                        allItems.value = allItems.value.map { current ->
                            if (current.id == item.id) current.toBuilder().insight(insight).build() else current
                        }
                    }
                } catch (_: Exception) {
                    // local fallback handled inside generator
                }
            }
        }
    }

    private fun splitLocal(query: String): List<String> {
        val parts = query.split("[\\s,.!?;:，。！？；：]+".toRegex()).filter { it.isNotEmpty() }
        return parts.ifEmpty { listOf(query) }
    }

    private fun List<FeedItem>.toScreenState(): FeedScreenState {
        return if (isEmpty()) FeedScreenState.Empty else FeedScreenState.Content
    }
}

sealed interface FeedScreenState {
    data object Loading : FeedScreenState
    data object Content : FeedScreenState
    data object Empty : FeedScreenState
    data class Error(val message: String) : FeedScreenState
}

sealed interface LoadMoreState {
    data object Idle : LoadMoreState
    data object Loading : LoadMoreState
    data object EndReached : LoadMoreState
    data class Error(val message: String) : LoadMoreState
}
