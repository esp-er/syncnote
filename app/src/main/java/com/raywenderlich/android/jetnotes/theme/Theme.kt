package com.raywenderlich.android.jetnotes.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.lightColors
import androidx.compose.material.darkColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color

object AppColors{
  val rwGreen = Color(0xFF006837)
  val rwGreenDark = Color(0xFF004012)
  val lightBrown = Color(0xFFDDB284)
  val darkBrown = Color(0xFF723712)
  val primaryGreen = Color(0xFF05745B)
}


private val DarkThemeColors = darkColors(
  primary = Color.Black,
  primaryVariant = AppColors.rwGreenDark,
  secondary = AppColors.darkBrown,
  secondaryVariant = Color(0xFF1A1A20),
  onPrimary = Color.White,
  onSecondary = Color.LightGray,
  surface = Color(0xFF1A1A20),
  background = Color(0xFF0D0D0F)

)


//TODO: change the green to something else
private val LightThemeColors = lightColors(
  primary = AppColors.primaryGreen,
  primaryVariant = Color(0xFF00F884),
  secondaryVariant = AppColors.primaryGreen,
  secondary = AppColors.lightBrown,
  onPrimary = Color.Black,
  onSecondary = Color.White,
)


@Composable
fun JetNotesTheme(content: @Composable () -> Unit) {
  val isDarkThemeEnabled = isSystemInDarkTheme() || JetNotesThemeSettings.isDarkThemeEnabled
  val colors = if (isDarkThemeEnabled) DarkThemeColors else LightThemeColors

  MaterialTheme(colors = colors, content = content)
}

/**
 * Allows changing between light and a dark theme from the app's settings.
 */
object JetNotesThemeSettings {
  var isDarkThemeEnabled by mutableStateOf(false)
}
