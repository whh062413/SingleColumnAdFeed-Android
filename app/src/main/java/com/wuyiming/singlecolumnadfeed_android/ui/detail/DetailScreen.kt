package com.wuyiming.singlecolumnadfeed_android.ui.detail

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.wuyiming.singlecolumnadfeed_android.data.model.FeedItem
import com.wuyiming.singlecolumnadfeed_android.data.model.FeedItemType
import com.wuyiming.singlecolumnadfeed_android.ui.theme.AdFeedTheme
import com.wuyiming.singlecolumnadfeed_android.viewmodel.FeedViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    itemId: String,
    viewModel: FeedViewModel,
    onBack: () -> Unit
) {
    val items by viewModel.allFeedItems.collectAsState()
    val item = items.firstOrNull { it.id == itemId } ?: return

    AdFeedTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Detail", maxLines = 1) },
                    navigationIcon = {
                        IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back") }
                    }
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState())
            ) {
                when (item.type) {
                    FeedItemType.IMAGE_BIG -> {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current).data(item.coverUrl).crossfade(true).build(),
                            contentDescription = "Cover", modifier = Modifier.fillMaxWidth().height(300.dp),
                            contentScale = ContentScale.Crop
                        )
                    }
                    FeedItemType.IMAGE_SMALL -> {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current).data(item.coverUrl).crossfade(true).build(),
                            contentDescription = "Cover", modifier = Modifier.fillMaxWidth().height(250.dp),
                            contentScale = ContentScale.Crop
                        )
                    }
                    FeedItemType.VIDEO -> {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current).data(item.coverUrl).crossfade(true).build(),
                            contentDescription = "Cover", modifier = Modifier.fillMaxWidth().height(300.dp),
                            contentScale = ContentScale.Crop
                        )
                    }
                }

                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = item.title, fontSize = 22.sp, fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface)
                    Spacer(Modifier.height(12.dp))
                    Text(text = item.description, fontSize = 15.sp, lineHeight = 24.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f))

                    if (item.insight?.summary != null || (item.insight?.tags?.isNotEmpty() == true)) {
                        Spacer(Modifier.height(20.dp))
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("AI 分析", fontSize = 14.sp, fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.primary)
                                Spacer(Modifier.height(8.dp))
                                if (item.insight?.summary != null) {
                                    Text(item.insight.summary, fontSize = 14.sp,
                                        color = MaterialTheme.colorScheme.onSurface)
                                    Spacer(Modifier.height(8.dp))
                                }
                                if (item.insight?.tags?.isNotEmpty() == true) {
                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        item.insight.tags.forEach { tag ->
                                            SuggestionChip(onClick = {}, label = { Text(tag, fontSize = 12.sp) })
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(24.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.clip(RoundedCornerShape(8.dp)).clickable { viewModel.toggleLike(item.id) }.padding(12.dp)
                        ) {
                            Icon(
                                imageVector = if (item.isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = "Like",
                                tint = if (item.isLiked) Color(0xFFEA4335) else MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(Modifier.height(4.dp))
                            Text("${item.likesCount}", fontSize = 14.sp)
                        }
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.clip(RoundedCornerShape(8.dp)).clickable { viewModel.toggleCollect(item.id) }.padding(12.dp)
                        ) {
                            Icon(
                                imageVector = if (item.isCollected) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                contentDescription = "Collect",
                                tint = if (item.isCollected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
