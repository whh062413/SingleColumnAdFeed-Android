package com.wuyiming.singlecolumnadfeed_android.ui.detail

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
    item: FeedItem,
    viewModel: FeedViewModel,
    onBack: () -> Unit
) {
    var liked by remember { mutableStateOf(item.isLiked()) }

    AdFeedTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Detail", maxLines = 1) },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                        }
                    }
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
            ) {
                // Media
                when (item.getFeedItemType()) {
                    FeedItemType.BIG_IMAGE -> {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(item.getCoverUrl() ?: "")
                                .crossfade(true)
                                .build(),
                            contentDescription = "Cover",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(300.dp),
                            contentScale = ContentScale.Crop
                        )
                    }
                    FeedItemType.SMALL_IMAGE -> {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(item.getCoverUrl() ?: "")
                                .crossfade(true)
                                .build(),
                            contentDescription = "Cover",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(250.dp),
                            contentScale = ContentScale.Crop
                        )
                    }
                    FeedItemType.VIDEO -> {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(item.getCoverUrl() ?: "")
                                .crossfade(true)
                                .build(),
                            contentDescription = "Cover",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(300.dp),
                            contentScale = ContentScale.Crop
                        )
                    }
                }

                // Content
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = item.getTitle(),
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = item.getDescription() ?: "",
                        fontSize = 15.sp,
                        lineHeight = 24.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                    )

                    // AI Insight
                    if (item.getInsight() != null) {
                        Spacer(modifier = Modifier.height(20.dp))
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "AI 分析",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = item.getInsight().getSummary() ?: "",
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                if (item.getInsight().getTags() != null) {
                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        item.getInsight().getTags().forEach { tag ->
                                            SuggestionChip(
                                                onClick = {},
                                                label = { Text(tag, fontSize = 12.sp) }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Interaction bar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable {
                                    liked = !liked
                                    viewModel.toggleLike(item.getId())
                                }
                                .padding(12.dp)
                        ) {
                            Icon(
                                imageVector = if (liked) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                                contentDescription = "Like",
                                tint = if (liked) Color(0xFFEA4335) else MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("", fontSize = 14.sp)
                        }
                    }
                }
            }
        }
    }
}
