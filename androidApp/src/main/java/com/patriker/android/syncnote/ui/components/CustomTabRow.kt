package com.patriker.android.syncnote.ui.components

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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.platform.LocalDensity
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
    backgroundColor: Color = MaterialTheme.colors.background,
    contentColor: Color = contentColorFor(backgroundColor),
    indicatorShape: Shape = RectangleShape,
    fillFraction: Float = 1f,
    indicator: @Composable (tabPositions: List<TabPos>) -> Unit = @Composable { tabPositions ->
        TabRowDefaults.CustomIndicator(
            Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
            shape = indicatorShape,
            fillFraction = fillFraction,
            color = MaterialTheme.colors.primaryVariant.copy(alpha=0.7f)
        )
    },
    divider: @Composable () -> Unit = @Composable {
        //TabRowDefaults.Divider(modifier = Modifier.widthIn(max=100.dp), thickness = 0.8.dp, color = MaterialTheme.colors.primaryVariant)
        CustomDivider(color = MaterialTheme.colors.primaryVariant.copy(alpha = 0.9f), thickness = 0.5.dp)
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



                subcompose(TabSlots.Indicator) {
                    indicator(tabPositions)
                }.forEach {
                    it.measure(Constraints.fixed(tabRowWidth, tabRowHeight)).placeRelative(0, 0)
                }

                //Sum the widths of each tab to the left and place it at the sum width
                tabPlaceables.forEachIndexed { index, placeable ->
                    placeable.placeRelative(tabWidths.take(index).sum() , 0)
                }

                subcompose(TabSlots.Divider, divider).forEach {
                    val placeable = it.measure(constraints.copy(minHeight = 0))
                    placeable.placeRelative(0, tabRowHeight - placeable.height)
                }



            }
        }
    }
}

@Composable
fun CustomDivider(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colors.onSurface.copy(alpha = 0.9f),
    thickness: Dp = 1.dp,
    startIndent: Dp = 0.dp,
    endIndent: Dp = 0.dp
) {
    val indentMod = if (startIndent.value != 0f) {
        Modifier.padding(start = startIndent, end = endIndent)
    } else {
        Modifier
    }
    val targetThickness = if (thickness == Dp.Hairline) {
        (1f / LocalDensity.current.density).dp
    } else {
        thickness
    }
    Box(
        modifier
            .then(indentMod)
            .fillMaxWidth()
            .height(targetThickness)
            .background(color = color)
    )
}

@Composable
fun TabRowDefaults.CustomIndicator(
    modifier: Modifier = Modifier,
    height: Dp = TabRowDefaults.IndicatorHeight,
    color: Color = LocalContentColor.current,
    shape: Shape = RectangleShape,
    fillFraction: Float = 1f,
) {
    Row(verticalAlignment = Alignment.CenterVertically){
        Box(
            modifier
                .align(Alignment.CenterVertically)
                .clip(shape)
                .fillMaxWidth(fillFraction)
                .fillMaxHeight(fillFraction)
                .height(height)
                .background(color = color)
        )
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



