package com.wuyiming.singlecolumnadfeed_android.ui.detail

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.wuyiming.singlecolumnadfeed_android.data.model.FeedItem
import com.wuyiming.singlecolumnadfeed_android.viewmodel.FeedViewModel

class DetailActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val feedItem: FeedItem? = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra("feed_item", FeedItem::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getSerializableExtra("feed_item") as? FeedItem
        }

        if (feedItem == null) {
            Toast.makeText(this, "Item not found", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val viewModel = FeedViewModel.getSharedInstance(application)

        setContent {
            DetailScreen(
                item = feedItem,
                viewModel = viewModel,
                onBack = { finish() }
            )
        }
    }
}
