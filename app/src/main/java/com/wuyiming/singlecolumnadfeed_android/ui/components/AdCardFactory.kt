package com.wuyiming.singlecolumnadfeed_android.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import coil.compose.AsyncImage
import coil.request.ImageRequest

import com.wuyiming.singlecolumnadfeed_android.data.model.FeedItem
import com.wuyiming.singlecolumnadfeed_android.data.model.FeedItemType

@Composable
fun AdCardFactory(
    item: FeedItem,
    onCardClick: () -> Unit,
    onLikeClick: () -> Unit,
    onCommentClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { onCardClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column {
            // Cover Media
            when (item.getFeedItemType()) {
                FeedItemType.BIG_IMAGE -> BigImageCover(item.getCoverUrl())
                FeedItemType.SMALL_IMAGE -> SmallImageCover(item.getCoverUrl())
                FeedItemType.VIDEO -> BigImageCover(item.getCoverUrl())
            }

            // Content
            Column(modifier = Modifier.padding(12.dp)) {
                // AI Tags
                if (item.getInsight() != null && item.getInsight().getTags() != null) {
                    Row(
                        modifier = Modifier.padding(bottom = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        item.getInsight().getTags().take(3).forEach { tag ->
                            SuggestionChip(
                                onClick = {},
                                label = {
                                    Text(tag, fontSize = 11.sp)
                                },
                                modifier = Modifier.height(24.dp)
                            )
                        }
                    }
                }

                // Title
                Text(
                    text = item.getTitle(),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface
                )

                // AI Summary
                if (item.getInsight() != null && item.getInsight().getSummary() != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = item.getInsight().getSummary(),
                        fontSize = 13.sp,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Interaction bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Like
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { onLikeClick() }
                    ) {
                        Icon(
                            imageVector = if (item.isLiked()) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                            contentDescription = "Like",
                            tint = if (item.isLiked()) Color(0xFFEA4335) else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                    }

                    // Comment
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { onCommentClick() }
                    ) {
                        Text(
                            text = "评论",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BigImageCover(coverUrl: String?) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(coverUrl ?: "")
                .crossfade(true)
                .build(),
            contentDescription = "Cover",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    }
}

@Composable
fun SmallImageCover(coverUrl: String?) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(coverUrl ?: "")
                .crossfade(true)
                .build(),
            contentDescription = "Cover",
            modifier = Modifier
                .size(100.dp)
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop
        )
    }
}

