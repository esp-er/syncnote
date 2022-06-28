package com.patriker.syncnote.ui

import com.raywenderlich.jetnotes.theme.*
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Typography
import androidx.compose.material.lightColors
import androidx.compose.material.darkColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.platform.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontFamily.Companion.Monospace



@Composable
fun SyncNoteDesktopTheme(content: @Composable () -> Unit) {
    val isDarkThemeEnabled = isSystemInDarkTheme() || ThemeSettings.isDarkThemeEnabled
    val colors = if (isDarkThemeEnabled) DarkThemeColors else LightThemeColors

    val fontFamily = FontFamily(
        Font(
            resource = "Rubik-Variable.ttf",
            weight = FontWeight.W400,
            style = FontStyle.Normal
        )
    )

    val typography = Typography(defaultFontFamily = fontFamily)

    MaterialTheme(colors = colors, content = content, typography = typography)

}

