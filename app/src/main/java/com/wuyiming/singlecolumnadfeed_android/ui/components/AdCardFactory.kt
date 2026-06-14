package com.wuyiming.singlecolumnadfeed_android.ui.components

import android.view.LayoutInflater
import android.net.Uri
import androidx.annotation.OptIn
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.PlayerView
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.wuyiming.singlecolumnadfeed_android.R
import com.wuyiming.singlecolumnadfeed_android.data.model.FeedItem
import com.wuyiming.singlecolumnadfeed_android.data.model.FeedItemType
import com.wuyiming.singlecolumnadfeed_android.data.video.VideoCacheManager
import kotlinx.coroutines.launch

@OptIn(UnstableApi::class)
@Composable
fun AdCardFactory(
    item: FeedItem,
    onLikeClick: (String) -> Unit,
    onCollectClick: (String) -> Unit,
    onCommentClick: (String) -> Unit,
    onTagClick: (String) -> Unit = {},
    onCardClick: (String) -> Unit,
    shouldAutoPlayVideo: Boolean = false,
    shouldPreloadVideo: Boolean = false,
    modifier: Modifier = Modifier
) {
    when (item.type) {
        FeedItemType.IMAGE_BIG -> BigImageAdCard(item, onLikeClick, onCollectClick, onCommentClick, onCardClick, modifier)
        FeedItemType.IMAGE_SMALL -> SmallImageAdCard(item, onLikeClick, onCollectClick, onCommentClick, onCardClick, modifier)
        FeedItemType.VIDEO -> VideoAdCard(item, onLikeClick, onCollectClick, onCommentClick, onCardClick, shouldAutoPlayVideo, shouldPreloadVideo, modifier)
    }
}

@Composable
fun BigImageAdCard(
    item: FeedItem,
    onLikeClick: (String) -> Unit,
    onCollectClick: (String) -> Unit,
    onCommentClick: (String) -> Unit,
    onCardClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp).clickable { onCardClick(item.id) },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column {
            AdCoverImage(item, Modifier.fillMaxWidth().height(220.dp))
            AdCardContent(item, onLikeClick, onCollectClick, onCommentClick)
        }
    }
}

@Composable
fun SmallImageAdCard(
    item: FeedItem,
    onLikeClick: (String) -> Unit,
    onCollectClick: (String) -> Unit,
    onCommentClick: (String) -> Unit,
    onCardClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp).clickable { onCardClick(item.id) },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(modifier = Modifier.padding(12.dp)) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current).data(item.coverUrl).crossfade(true).build(),
                contentDescription = item.title,
                modifier = Modifier.size(100.dp).clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier.weight(1f).padding(start = 12.dp)) {
                Text(text = item.title, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, maxLines = 2, overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface)
                Spacer(Modifier.height(4.dp))
                Text(text = item.description, fontSize = 13.sp, maxLines = 2, overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                AdInteractionBar(item, onLikeClick, onCollectClick, onCommentClick)
            }
        }
    }
}

@OptIn(UnstableApi::class)
@Composable
fun VideoAdCard(
    item: FeedItem,
    onLikeClick: (String) -> Unit,
    onCollectClick: (String) -> Unit,
    onCommentClick: (String) -> Unit,
    onCardClick: (String) -> Unit,
    shouldAutoPlay: Boolean = false,
    shouldPreload: Boolean = false,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val player = remember { FeedVideoPlayerPool.acquire(context, item.id) }
    var isPlaying by remember { mutableStateOf(false) }
    var isMuted by remember { mutableStateOf(true) }
    var showFullscreen by remember { mutableStateOf(false) }
    var hasError by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    DisposableEffect(item.id) {
        onDispose { if (!shouldAutoPlay) FeedVideoPlayerPool.release(item.id) }
    }

    LaunchedEffect(shouldAutoPlay) {
        if (shouldAutoPlay && !hasError) {
            isLoading = true
            try {
                val videoUrl = item.videoUrl ?: ""
                val uri = if (videoUrl.isNotEmpty()) VideoCacheManager.getPlayableVideoUri(context, videoUrl) else null
                if (uri != null) {
                    player.setMediaItem(MediaItem.fromUri(uri))
                    player.prepare()
                    player.playWhenReady = true
                    player.volume = if (isMuted) 0f else 1f
                    isPlaying = true
                }
            } catch (_: Exception) { hasError = true }
            isLoading = false
        } else if (!shouldAutoPlay && isPlaying) {
            player.pause()
            isPlaying = false
        }
    }

    LaunchedEffect(shouldPreload) {
        if (shouldPreload && !hasError && !isPlaying) {
            try {
                val videoUrl = item.videoUrl ?: ""
                if (videoUrl.isNotEmpty()) VideoCacheManager.getPlayableVideoUri(context, videoUrl)
            } catch (_: Exception) {}
        }
    }

    Card(
        modifier = modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp).clickable { onCardClick(item.id) },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column {
            Box(modifier = Modifier.fillMaxWidth().aspectRatio(16f / 9f)) {
                if (isPlaying && !hasError) {
                    AndroidView(
                        factory = { context ->
                            PlayerView(context).apply { this.player = player }
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    AdCoverImage(item, Modifier.fillMaxSize())
                }

                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center).size(40.dp),
                        color = Color.White, strokeWidth = 3.dp
                    )
                }

                if (!isPlaying && !hasError) {
                    IconButton(
                        onClick = {
                            scope.launch {
                                isLoading = true
                                try {
                                    val videoUrl = item.videoUrl ?: ""
                                    val uri = if (videoUrl.isNotEmpty()) VideoCacheManager.getPlayableVideoUri(context, videoUrl) else null
                                    if (uri != null) {
                                        player.setMediaItem(MediaItem.fromUri(uri))
                                        player.prepare()
                                        player.playWhenReady = true
                                        player.volume = if (isMuted) 0f else 1f
                                        isPlaying = true
                                    }
                                } catch (_: Exception) { hasError = true }
                                isLoading = false
                            }
                        },
                        modifier = Modifier.align(Alignment.Center)
                    ) {
                        val infiniteTransition = rememberInfiniteTransition(label = "playPulse")
                        val pulseScale by infiniteTransition.animateFloat(
                            initialValue = 1f, targetValue = 1.12f,
                            animationSpec = infiniteRepeatable(animation = tween(600, easing = FastOutSlowInEasing), repeatMode = RepeatMode.Reverse),
                            label = "playPulseScale"
                        )
                        Surface(
                            shape = RoundedCornerShape(50), color = Color.Black.copy(alpha = 0.5f),
                            modifier = Modifier.graphicsLayer { scaleX = pulseScale; scaleY = pulseScale }
                        ) {
                            Icon(Icons.Default.PlayArrow, "Play", tint = Color.White, modifier = Modifier.size(64.dp))
                        }
                    }
                }

                if (isPlaying) {
                    Row(
                        modifier = Modifier.align(Alignment.TopEnd).padding(8.dp),
                        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp)
                    ) {
                        IconButton(
                            onClick = { isMuted = !isMuted; player.volume = if (isMuted) 0f else 1f },
                            modifier = Modifier.size(36.dp).background(Color.Black.copy(alpha = 0.4f), RoundedCornerShape(20))
                        ) {
                            Icon(if (isMuted) Icons.Default.VolumeOff else Icons.Default.VolumeUp, "Mute", tint = Color.White, modifier = Modifier.size(18.dp))
                        }
                        IconButton(
                            onClick = { player.pause(); showFullscreen = true },
                            modifier = Modifier.size(36.dp).background(Color.Black.copy(alpha = 0.4f), RoundedCornerShape(20))
                        ) {
                            Icon(Icons.Default.Fullscreen, "Fullscreen", tint = Color.White, modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }
            AdCardContent(item, onLikeClick, onCollectClick, onCommentClick)
        }
    }

    if (showFullscreen) {
        Dialog(
            onDismissRequest = { showFullscreen = false; player.playWhenReady = true },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
                AndroidView(
                    factory = { context -> PlayerView(context).apply { this.player = player; useController = true } },
                    modifier = Modifier.fillMaxSize()
                )
                IconButton(
                    onClick = { showFullscreen = false; player.playWhenReady = true },
                    modifier = Modifier.align(Alignment.TopEnd).padding(16.dp)
                ) {
                    Icon(Icons.Default.Close, "Close", tint = Color.White, modifier = Modifier.size(32.dp))
                }
            }
        }
    }
}

@Composable
private fun AdCoverImage(item: FeedItem, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    var imageLoaded by remember(item.id) { mutableStateOf(false) }

    Box(modifier = modifier.clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)).background(coverBrush())) {
        Image(painter = painterResource(id = localFallbackCoverRes(item)), contentDescription = item.title,
            contentScale = ContentScale.Crop, modifier = Modifier.matchParentSize())

        AsyncImage(
            model = ImageRequest.Builder(context).data(item.coverUrl).crossfade(true).build(),
            contentDescription = item.title, contentScale = ContentScale.Crop,
            onSuccess = { imageLoaded = true }, onError = { imageLoaded = false },
            modifier = Modifier.matchParentSize().graphicsLayer { alpha = if (imageLoaded) 1f else 0f }
        )

        Box(modifier = Modifier.matchParentSize().background(Brush.verticalGradient(
            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.58f))
        )))
        Text(text = item.title, style = MaterialTheme.typography.titleLarge, color = Color.White,
            maxLines = 2, overflow = TextOverflow.Ellipsis,
            modifier = Modifier.align(Alignment.BottomStart).padding(18.dp))
    }
}

@Composable
private fun AdCardContent(
    item: FeedItem,
    onLikeClick: (String) -> Unit,
    onCollectClick: (String) -> Unit,
    onCommentClick: (String) -> Unit
) {
    Column(modifier = Modifier.padding(12.dp)) {
        if (item.insight?.tags != null && item.insight.tags.isNotEmpty()) {
            Row(modifier = Modifier.padding(bottom = 6.dp),
                horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(6.dp)) {
                item.insight.tags.take(3).forEach { tag ->
                    SuggestionChip(onClick = {}, label = { Text(tag, fontSize = 11.sp) }, modifier = Modifier.height(24.dp))
                }
            }
        }
        Text(text = item.title, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, maxLines = 2,
            overflow = TextOverflow.Ellipsis, color = MaterialTheme.colorScheme.onSurface)
        if (item.insight?.summary != null) {
            Spacer(Modifier.height(4.dp))
            Text(text = item.insight.summary, fontSize = 13.sp, maxLines = 2, overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
        }
        Spacer(Modifier.height(8.dp))
        AdInteractionBar(item, onLikeClick, onCollectClick, onCommentClick)
    }
}

@Composable
private fun AdInteractionBar(
    item: FeedItem,
    onLikeClick: (String) -> Unit,
    onCollectClick: (String) -> Unit,
    onCommentClick: (String) -> Unit
) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable { onLikeClick(item.id) }) {
            Icon(
                imageVector = if (item.isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                contentDescription = "Like", modifier = Modifier.size(20.dp),
                tint = if (item.isLiked) Color(0xFFEA4335) else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )
            Spacer(Modifier.width(4.dp))
            Text(text = "${item.likesCount}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
        }
        Spacer(Modifier.width(20.dp))
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable { onCommentClick(item.id) }) {
            Icon(Icons.Default.ChatBubbleOutline, "Comment", tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(4.dp))
            Text(text = "${item.commentsCount}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
        }
        Spacer(Modifier.weight(1f))
        IconButton(onClick = { onCollectClick(item.id) }) {
            Icon(
                imageVector = if (item.isCollected) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                contentDescription = "Collect", modifier = Modifier.size(20.dp),
                tint = if (item.isCollected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private fun localFallbackCoverRes(item: FeedItem): Int {
    val covers = listOf(R.drawable.ad_cover_1, R.drawable.ad_cover_2, R.drawable.ad_cover_3, R.drawable.ad_cover_4, R.drawable.ad_cover_5)
    return covers[kotlin.math.abs(item.id.hashCode()) % covers.size]
}

private fun coverBrush(): Brush {
    return Brush.linearGradient(colors = listOf(Color(0xFF1B5E20), Color(0xFF00695C), Color(0xFF455A64)))
}
