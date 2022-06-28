package com.patriker.syncnote.ui

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed

inline fun Modifier.noRippleClickable(interactionSource: MutableInteractionSource, crossinline onClick: ()->Unit): Modifier = composed {
    Modifier.hoverable(interactionSource = interactionSource).clickable(indication = null,
        interactionSource = interactionSource) {
        onClick()
    }
}
