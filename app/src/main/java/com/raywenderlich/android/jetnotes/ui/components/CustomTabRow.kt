package com.raywenderlich.android.jetnotes.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.*
import androidx.compose.material.TabPosition
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.platform.debugInspectorInfo
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

private enum class TabSlots {
    Tabs,
    Divider,
    Indicator
}


@Composable
fun CustomTabRow(
    selectedTabIndex: Int,
    tabWeights: List<Float>,
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colors.primarySurface,
    contentColor: Color = contentColorFor(backgroundColor),
    indicator: @Composable (tabPositions: List<TabPos>) -> Unit = @Composable { tabPositions ->
        TabRowDefaults.Indicator(
            Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex])
        )
    },
    divider: @Composable () -> Unit = @Composable {
        TabRowDefaults.Divider()
    },
    tabs: @Composable () -> Unit
) {
    Surface(
        modifier = modifier.selectableGroup(),
        color = backgroundColor,
        contentColor = contentColor
    ) {
        SubcomposeLayout(Modifier.fillMaxWidth()) { constraints ->
            val tabRowWidth = constraints.maxWidth
            val tabMeasurables = subcompose(TabSlots.Tabs, tabs)
            val tabCount = tabMeasurables.size
            val totalWeights = if(tabWeights.sum() <= 0) tabCount.toFloat() else tabWeights.sum()
            val tabWidths =
                if(tabWeights.size >= tabCount)
                    tabWeights.map{
                        (tabRowWidth *  it / totalWeights).toInt()}
                        .take(tabCount) //Note: taking the tabcount is a dirty hack
                else
                    tabWeights.map{ (tabRowWidth *  it / totalWeights).toInt() } +
                        (0..(tabCount - tabWeights.size)).map{ (tabRowWidth * 1f / totalWeights).toInt() }


            val tabPlaceables = tabMeasurables.mapIndexed{ index, it ->
                it.measure(constraints.copy(minWidth = tabWidths[index], maxWidth = tabWidths[index]))
            }

            val tabRowHeight = tabPlaceables.maxByOrNull { it.height }?.height ?: 0

            val tabPositions = List(tabCount) { index ->
                TabPos(tabWidths.take(index).sum().toDp(), tabWidths[index].toDp())
            }

            layout(tabRowWidth, tabRowHeight) {

                //Sum the widths of each tab to the left and place it at the sum width
                tabPlaceables.forEachIndexed { index, placeable ->
                    placeable.placeRelative(tabWidths.take(index).sum() , 0)
                }

                subcompose(TabSlots.Divider, divider).forEach {
                    val placeable = it.measure(constraints.copy(minHeight = 0))
                    placeable.placeRelative(0, tabRowHeight - placeable.height)
                }

                subcompose(TabSlots.Indicator) {
                    indicator(tabPositions)
                }.forEach {
                    it.measure(Constraints.fixed(tabRowWidth, tabRowHeight)).placeRelative(0, 0)
                }
            }
        }
    }
}

fun Modifier.tabIndicatorOffset(
    currentTabPosition: TabPos
): Modifier = composed(
    inspectorInfo = debugInspectorInfo {
        name = "tabIndicatorOffset"
        value = currentTabPosition
    }
) {
    val currentTabWidth by animateDpAsState(
        targetValue = currentTabPosition.width,
        animationSpec = tween(durationMillis = 150, easing = FastOutSlowInEasing)
    )
    val indicatorOffset by animateDpAsState(
        targetValue = currentTabPosition.left,
        animationSpec = tween(durationMillis = 150, easing = FastOutSlowInEasing)
    )
    fillMaxWidth()
        .wrapContentSize(Alignment.BottomStart)
        .offset(x = indicatorOffset)
        .width(currentTabWidth)
}


@Immutable
class TabPos internal constructor(val left: Dp, val width: Dp) {
    val right: Dp get() = left + width

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is TabPosition) return false

        if (left != other.left) return false
        if (width != other.width) return false

        return true
    }

    override fun hashCode(): Int {
        var result = left.hashCode()
        result = 31 * result + width.hashCode()
        return result
    }

    override fun toString(): String {
        return "TabPosition(left=$left, right=$right, width=$width)"
    }
}



