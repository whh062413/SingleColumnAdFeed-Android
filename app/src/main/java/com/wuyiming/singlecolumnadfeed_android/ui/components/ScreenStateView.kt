package com.wuyiming.singlecolumnadfeed_android.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun FeedSkeletonList(modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 14.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(5) { SkeletonCard() }
    }
}

@Composable
fun FeedEmptyState(modifier: Modifier = Modifier) {
    StateMessage(
        icon = {
            Icon(
                imageVector = Icons.Default.Search, contentDescription = null,
                modifier = Modifier.size(48.dp), tint = MaterialTheme.colorScheme.primary
            )
        },
        title = "暂无广告内容",
        message = "当前频道还没有可展示的广告内容。",
        modifier = modifier
    )
}

@Composable
fun FeedErrorState(message: String, onRetry: () -> Unit, modifier: Modifier = Modifier) {
    StateMessage(
        icon = {
            Icon(
                imageVector = Icons.Default.Warning, contentDescription = null,
                modifier = Modifier.size(48.dp), tint = MaterialTheme.colorScheme.error
            )
        },
        title = "加载失败",
        message = message,
        modifier = modifier,
        action = {
            Button(onClick = onRetry) {
                Icon(imageVector = Icons.Default.Refresh, contentDescription = null)
                Text(text = "重试", modifier = Modifier.padding(start = 6.dp))
            }
        }
    )
}

@Composable
private fun SkeletonCard(modifier: Modifier = Modifier) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp,
        modifier = modifier.fillMaxWidth()
    ) {
        Column {
            SkeletonBlock(
                Modifier.fillMaxWidth().height(190.dp)
                    .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
            )
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                SkeletonBlock(Modifier.fillMaxWidth(0.72f).height(20.dp).clip(RoundedCornerShape(5.dp)))
                SkeletonBlock(Modifier.fillMaxWidth().height(14.dp).clip(RoundedCornerShape(5.dp)))
                SkeletonBlock(Modifier.fillMaxWidth(0.82f).height(14.dp).clip(RoundedCornerShape(5.dp)))
                SkeletonBlock(Modifier.width(160.dp).height(28.dp).clip(RoundedCornerShape(8.dp)))
            }
        }
    }
}

@Composable
private fun SkeletonBlock(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "skeletonShimmer")
    val shimmerOffset by transition.animateFloat(
        initialValue = -1f, targetValue = 2f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1_100, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ), label = "skeletonShimmerOffset"
    )
    val baseColor = MaterialTheme.colorScheme.surfaceVariant
    val highlightColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f)
    Box(
        modifier = modifier.background(
            Brush.linearGradient(
                colorStops = arrayOf(
                    0f to baseColor,
                    shimmerOffset.coerceIn(0f, 1f) to highlightColor,
                    1f to baseColor
                )
            )
        )
    )
}

@Composable
private fun StateMessage(
    icon: @Composable () -> Unit,
    title: String,
    message: String,
    modifier: Modifier = Modifier,
    action: (@Composable () -> Unit)? = null
) {
    Column(
        modifier = modifier.fillMaxSize().padding(28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        icon()
        Text(text = title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(top = 14.dp))
        Text(text = message, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 8.dp))
        if (action != null) Box(modifier = Modifier.padding(top = 18.dp)) { action() }
    }
}
