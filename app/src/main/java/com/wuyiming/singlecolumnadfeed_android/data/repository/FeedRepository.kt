package com.wuyiming.singlecolumnadfeed_android.data.repository

import com.wuyiming.singlecolumnadfeed_android.data.model.AdInsight
import com.wuyiming.singlecolumnadfeed_android.data.model.FeedItem

interface FeedRepository {
    suspend fun loadFeedItems(page: Int, pageSize: Int, refreshSeed: Int): List<FeedItem>
    suspend fun generateAiInsight(item: FeedItem): AdInsight?
    fun toggleLike(item: FeedItem): FeedItem
    fun toggleCollect(item: FeedItem): FeedItem
    fun getItemById(feedId: String): FeedItem?
}
