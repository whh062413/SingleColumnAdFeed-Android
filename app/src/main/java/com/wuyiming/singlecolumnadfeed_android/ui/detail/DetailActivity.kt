package com.wuyiming.singlecolumnadfeed_android.ui.detail

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.wuyiming.singlecolumnadfeed_android.viewmodel.FeedViewModel

class DetailActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val itemId = intent.getStringExtra("item_id")
        if (itemId == null) {
            Toast.makeText(this, "Item not found", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val viewModel = FeedViewModel.getSharedInstance(application)

        setContent {
            DetailScreen(itemId = itemId, viewModel = viewModel, onBack = { finish() })
        }
    }
}
