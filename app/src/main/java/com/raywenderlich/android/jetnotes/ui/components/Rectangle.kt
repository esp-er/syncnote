package com.raywenderlich.android.jetnotes.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp

@Composable
fun Rectangle(color: Color,
              height: Dp,
              modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.composed {
                height(height)
                .clip(RectangleShape)
                .background(color)
        }
    )
}
