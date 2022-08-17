package com.patriker.syncnote.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.lightColors
import androidx.compose.material.darkColors
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color

object SharedAppColors{
  val rwGreen = Color(0xFF006837)
  val rwGreenDark = Color(0xFF004012)
  val darkBrown = Color(0xFF723712)
  val primaryGreen = Color(0xFF05745B)
  val primaryBlue = Color(0xFF3584e4)
  val lightBrown = Color(0xFFDDB284)
  val darkGrey = Color(0xFF444444)
}


val DarkThemeColorsShared = darkColors(
  primary = SharedAppColors.primaryBlue,
  primaryVariant = Color(0xFF5E5C5E),
  onPrimary = Color.White,
  secondary = Color(0xFF3584e4),
  secondaryVariant = Color(0xFF365BCA),
  onSecondary = Color(0xFF7C7A7C),
  error = Color(0xFFc01c28),
  surface = Color(0xFF363436),
  background = Color(0xFF222022)

)


//TODO: change the green to something else
val LightThemeColorsShared = lightColors(
  primary = SharedAppColors.primaryBlue,
  primaryVariant = Color(0xFFE6E6E6),
  onPrimary = Color.Black,
  onSecondary = SharedAppColors.darkGrey,
  secondary = Color(0xFF3584e4),
  secondaryVariant = SharedAppColors.primaryGreen,
  error = Color(0xFFc01c28),
)


@Composable
fun SyncNoteThemeShared(content: @Composable () -> Unit) {
  var isDarkThemeEnabled = if(ThemeSettingsShared.isUserDefined) ThemeSettingsShared.isDarkThemeEnabled else isSystemInDarkTheme()
  LaunchedEffect(isDarkThemeEnabled){
    ThemeSettingsShared.isDarkThemeEnabledProp = isDarkThemeEnabled
  }
  val colors = if (isDarkThemeEnabled) DarkThemeColorsShared else LightThemeColorsShared

  MaterialTheme(colors = colors, content = content)
}

/**
 * Allows changing between light and a dark theme from the app's settings.
 */
object ThemeSettingsShared {
  var isDarkThemeEnabled by mutableStateOf(false)
  var isUserDefined by mutableStateOf(false)
  var isDarkThemeEnabledProp = false
}
