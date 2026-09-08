package com.nexora.kaamsetu.core.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColors = lightColorScheme(
    primary = KsBlue,
    onPrimary = KsOnPrimary,
    primaryContainer = KsBlueDark,
    secondary = KsOrange,
    background = KsBackground,
    surface = KsSurface,
    error = KsError
)

private val DarkColors = darkColorScheme(
    primary = KsBlue,
    onPrimary = KsOnPrimary,
    secondary = KsOrange,
    error = KsError
)

@Composable
fun KaamSetuTheme(
    useDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (useDarkTheme) DarkColors else LightColors
    MaterialTheme(
        colorScheme = colors,
        typography = KaamSetuTypography,
        content = content
    )
}
