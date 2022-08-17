package com.patriker.android.syncnote.ui.components


import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterialApi::class)
@Composable
fun <T> AnimatedSwipeDismiss(
    state: DismissState,
    modifier: Modifier = Modifier,
    item: T,
    background: @Composable (dismissedValue: DismissValue) -> Unit,
    content: @Composable (dismissedValue: DismissValue) -> Unit,
    directions: Set<DismissDirection> = setOf(DismissDirection.StartToEnd),
    enter: EnterTransition = expandVertically(),
    exit: ExitTransition = shrinkVertically(
        animationSpec = tween(
            durationMillis = 500,
        )
    ),
    onDismiss: (T) -> Unit
) {

    val isDismissed = state.isDismissed(DismissDirection.StartToEnd)

    AnimatedVisibility(
        modifier = modifier,
        visible = !isDismissed,
        enter = enter,
        exit = exit
    ) {
        SwipeToDismiss(
            modifier = modifier,
            state = state,
            directions = directions,
            background = { background(state.currentValue) },
            dismissContent = { content(state.currentValue) }
        )
    }
}