package com.patriker.android.syncnote.ui.components

import androidx.compose.ui.graphics.Shape
import androidx.compose.runtime.*
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.unit.*


@Composable
fun CustomDrawerShape(width: Float, height:Float) = object : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        return Outline.Rectangle(Rect(0f,0f,width, height))
    }
}
