package com.raywenderlich.jetnotes.theme

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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontFamily.Companion.Monospace

object AppColors{
  val rwGreen = Color(0xFF006837)
  val rwGreenDark = Color(0xFF004012)
  val lightBrown = Color(0xFFDDB284)
  val darkBrown = Color(0xFF723712)
  val primaryGreen = Color(0xFF05745B)
}


val DarkThemeColors = darkColors(
  primary = Color(0xFF3584e4),
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
val LightThemeColors = lightColors(
  primary = AppColors.primaryGreen,
  primaryVariant = Color(0xFF00F884),
  secondaryVariant = AppColors.primaryGreen,
  secondary = AppColors.lightBrown,
  error = Color(0xFFc01c28),
  onPrimary = Color.Black,
  onSecondary = Color.White,
)


@Composable
fun SyncNoteTheme(content: @Composable () -> Unit) {
  var isDarkThemeEnabled = if(ThemeSettings.isUserDefined) ThemeSettings.isDarkThemeEnabled else isSystemInDarkTheme()
  val colors = if (isDarkThemeEnabled) DarkThemeColors else LightThemeColors

  MaterialTheme(colors = colors, content = content)
}

/**
 * Allows changing between light and a dark theme from the app's settings.
 */
object ThemeSettings {
  var isDarkThemeEnabled by mutableStateOf(false)
  var isUserDefined by mutableStateOf(false)
}
