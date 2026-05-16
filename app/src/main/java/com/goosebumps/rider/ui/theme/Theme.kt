package com.goosebumps.rider.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Goosebumps Rider Brand Colors
val GbOrange = Color(0xFFFF6B35)
val GbOrangeDim = Color(0xFFCC5528)
val GbGreen = Color(0xFF4CAF50)
val GbRed = Color(0xFFE53935)
val GbYellow = Color(0xFFFFD600)
val GbSurface = Color(0xFF1A1A1A)
val GbSurface2 = Color(0xFF242424)
val GbSurface3 = Color(0xFF2E2E2E)
val GbBackground = Color(0xFF0D0D0D)
val GbOnSurface = Color(0xFFE8E8E8)
val GbOnSurfaceDim = Color(0xFF9E9E9E)
val GbDivider = Color(0xFF333333)

private val DarkColorScheme = darkColorScheme(
    primary = GbOrange,
    onPrimary = Color.White,
    primaryContainer = GbOrangeDim,
    onPrimaryContainer = Color.White,
    secondary = GbGreen,
    onSecondary = Color.White,
    tertiary = GbYellow,
    background = GbBackground,
    onBackground = GbOnSurface,
    surface = GbSurface,
    onSurface = GbOnSurface,
    surfaceVariant = GbSurface2,
    onSurfaceVariant = GbOnSurfaceDim,
    error = GbRed,
    onError = Color.White,
    outline = GbDivider
)

@Composable
fun GoosebumpsRiderTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = RiderTypography,
        content = content
    )
}
