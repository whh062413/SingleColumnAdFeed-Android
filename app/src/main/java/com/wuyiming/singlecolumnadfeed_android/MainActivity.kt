package com.wuyiming.singlecolumnadfeed_android

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.wuyiming.singlecolumnadfeed_android.ui.detail.DetailActivity
import com.wuyiming.singlecolumnadfeed_android.ui.feed.FeedScreen
import com.wuyiming.singlecolumnadfeed_android.ui.theme.AdFeedTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            AdFeedTheme {
                FeedScreen(
                    onItemClick = { item -> navigateToDetail(item) }
                )
            }
        }
    }

    private fun navigateToDetail(item: com.wuyiming.singlecolumnadfeed_android.data.model.FeedItem) {
        val intent = Intent(this, DetailActivity::class.java)
        intent.putExtra("feed_item", item)
        startActivity(intent)
    }
}