package com.wuyiming.singlecolumnadfeed_android.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColors = lightColorScheme(
    primary = Color(0xFF1A73E8),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD2E3FC),
    secondary = Color(0xFF5F6368),
    surface = Color(0xFFF8F9FA),
    background = Color(0xFFFFFFFF),
    onBackground = Color(0xFF202124),
    onSurface = Color(0xFF202124),
    outline = Color(0xFFDADCE0),
    error = Color(0xFFEA4335)
)

@Composable
fun AdFeedTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColors,
        content = content
    )
}
