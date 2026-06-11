package com.wuyiming.singlecolumnadfeed_android

import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.wuyiming.singlecolumnadfeed_android.data.model.FeedItem
import com.wuyiming.singlecolumnadfeed_android.ui.detail.DetailActivity
import com.wuyiming.singlecolumnadfeed_android.ui.feed.FeedScreen
import com.wuyiming.singlecolumnadfeed_android.ui.theme.AdFeedTheme

/**
 * Compose UI bridge — provides Java-friendly hooks for MainActivity.
 */
object MainContent {

    /**
     * Called from MainActivity.java to set up Compose content.
     */
    @JvmStatic
    fun setupCompose(activity: ComponentActivity) {
        activity.setContent {
            AdFeedTheme {
                FeedScreen(
                    onItemClick = { item ->
                        val intent = Intent(activity, DetailActivity::class.java)
                        intent.putExtra("feed_item", item)
                        activity.startActivity(intent)
                    }
                )
            }
        }
    }
}