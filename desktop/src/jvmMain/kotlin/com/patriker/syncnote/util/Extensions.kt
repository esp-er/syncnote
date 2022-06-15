package com.patriker.syncnote.util

import androidx.compose.ui.graphics.Color
import java.awt.Color.*

fun awtColor(hex: String): java.awt.Color {
  val c = hex.drop(4)
  return java.awt.Color.decode("#$c")
}

fun Color.Companion.fromHex(hex: String): Color {
  val c = awtColor(hex)
  val rgbArr = c.getRGBColorComponents(null)
  return Color(red = rgbArr[0], green = rgbArr[1],  blue = rgbArr[2], 1f)
}
