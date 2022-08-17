package com.patriker.android.syncnote.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp


@Composable
fun horLineSeparator(lineColor: Color = MaterialTheme.colors.primaryVariant){
    Row(modifier = Modifier
        .fillMaxWidth()
        .height(1.dp)
        .drawBehind {
            drawLine(lineColor, Offset(22f, 0f), Offset(size.width-22f, 0f), 1f)
        }) { }
}
