package com.patriker.syncnote.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.platform.Font
import androidx.compose.ui.text.font.FontFamily
import com.patriker.syncnote.theme.DarkThemeColorsShared
import com.patriker.syncnote.theme.LightThemeColorsShared


@Composable
fun SyncNoteDesktopTheme(content: @Composable () -> Unit) {
    var isDarkThemeEnabled = if(ThemeSettingsDesktop.isUserDefined) ThemeSettingsDesktop.isDarkThemeEnabled else isSystemInDarkTheme()
    val colors = if (isDarkThemeEnabled) DarkThemeColorsShared else LightThemeColorsShared


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

