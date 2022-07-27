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
    var isDarkThemeEnabled = if(ThemeSettingsDesktop.isUserDefined) ThemeSettingsDesktop.isDarkThemeEnabled else isSystemInDarkTheme()
    val colors = if (isDarkThemeEnabled) DarkThemeColors else LightThemeColors


    val fontFamilyDark = FontFamily(
        Font(
            resource = "Inter-Light-Hint.ttf",
        )
    )
    val fontFamilyLight = FontFamily(
        Font(
            resource = "Inter-Medium-Hint.ttf",
        )
    )

    val typography = if(isDarkThemeEnabled) Typography(defaultFontFamily = fontFamilyDark)
                    else Typography(defaultFontFamily = fontFamilyLight)

    MaterialTheme(colors = colors, content = content, typography = typography)

}

object ThemeSettingsDesktop {
    var isDarkThemeEnabled by mutableStateOf(false)
    var isUserDefined by mutableStateOf(false)
}

