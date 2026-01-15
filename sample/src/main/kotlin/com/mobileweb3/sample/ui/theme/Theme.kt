package com.mobileweb3.sample.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val Purple = Color(0xFF8B5CF6)
private val PurpleLight = Color(0xFFA78BFA)
private val PurpleDark = Color(0xFF7C3AED)
private val Green = Color(0xFF10B981)
private val Red = Color(0xFFEF4444)
private val Orange = Color(0xFFF59E0B)

private val DarkColorScheme = darkColorScheme(
    primary = Purple,
    onPrimary = Color.White,
    secondary = PurpleLight,
    tertiary = Green,
    error = Red,
    background = Color(0xFF0F0F1A),
    surface = Color(0xFF1A1A2E),
    surfaceVariant = Color(0xFF252540),
    onBackground = Color.White,
    onSurface = Color.White,
    onSurfaceVariant = Color(0xFFB0B0C0),
)

private val LightColorScheme = lightColorScheme(
    primary = PurpleDark,
    onPrimary = Color.White,
    secondary = Purple,
    tertiary = Green,
    error = Red,
    background = Color(0xFFF8F8FC),
    surface = Color.White,
    surfaceVariant = Color(0xFFF0F0F5),
    onBackground = Color(0xFF1A1A2E),
    onSurface = Color(0xFF1A1A2E),
    onSurfaceVariant = Color(0xFF606080),
)

@Composable
fun Web3DemoTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography(),
        content = content
    )
}
