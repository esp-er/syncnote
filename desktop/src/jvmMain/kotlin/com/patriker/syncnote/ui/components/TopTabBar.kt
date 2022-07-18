package com.patriker.syncnote.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.ripple.LocalRippleTheme
import androidx.compose.material.ripple.RippleAlpha
import androidx.compose.material.ripple.RippleTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.patriker.syncnote.ui.noRippleClickable
import com.patriker.syncnote.viewModel
import com.raywenderlich.jetnotes.routing.NotesRouter
import com.raywenderlich.jetnotes.routing.Screen
import compose.icons.TablerIcons
import compose.icons.Octicons
import compose.icons.octicons.Inbox24
import compose.icons.octicons.Trash24
import compose.icons.tablericons.*


//Desktop Ripple Theme
private object NoRippleTheme : RippleTheme {
    @Composable
    override fun defaultColor() = Color.Unspecified

    @Composable
    override fun rippleAlpha(): RippleAlpha = RippleAlpha(0.0f,0.0f,0.0f,0.0f)
}


@Composable
fun TopTabBar(initState: Int, isConnected: Boolean = false) {

    var state by remember{ mutableStateOf(initState) }

    val textStyle = TextStyle(color = MaterialTheme.colors.onSecondary, fontSize = 12.sp)
    val interactionSource = remember { MutableInteractionSource() }
    val interactionSource2 = remember { MutableInteractionSource() }
    val interactionSource3 = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()
    val isHovered2 by interactionSource2.collectIsHoveredAsState()
    val isHovered3  by interactionSource3.collectIsHoveredAsState()
    //TODO: make a new composable that can individually encapsulate interactionsource and hovered state

    var expandArchive by remember { mutableStateOf(false) }
    fun dismissArchiveDropDown() { expandArchive = false }
    Row {
        Column {
            CustomTabRow(
                modifier = Modifier.padding(horizontal = 8.dp),
                selectedTabIndex = state,
                tabWeights = listOf(3f, 3f, 1f),
                indicatorShape = RoundedCornerShape(2.dp)
            ) { //TODO: Find better way to pass in tab weights


                val surfColor = MaterialTheme.colors.surface

                fun tabBackground(hover: Boolean, selected: Int): Color {
                    return if (selected == state || !hover)
                        Color.Unspecified
                    else
                        surfColor
                }

                fun Modifier.tabHover(state: Int) =
                    when (state) {
                        0 -> Modifier.hoverable(interactionSource)
                            .background(tabBackground(isHovered, state))
                        1 -> Modifier.hoverable(interactionSource2)
                            .background(tabBackground(isHovered2, state))
                        else -> Modifier.hoverable(interactionSource3)
                            .background(tabBackground(isHovered3, state))
                    }


                CompositionLocalProvider(LocalRippleTheme provides NoRippleTheme) {
                    LeadingIconTab(
                        modifier = Modifier.tabHover(0),
                        text = {}, //TODO: fix text colors e.g primary / on prim, etc
                        icon = {
                            val selectedMod =
                                if (state == 0) { //TODO: How can this be done with currying / passing func instead of val
                                    Modifier
                                        .fillMaxSize(1f)
                                        .background(MaterialTheme.colors.primaryVariant)
                                } else {
                                    Modifier.fillMaxSize(1f)
                                }

                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 4.dp, bottom = 4.dp)
                            ) {
                                Icon(
                                    modifier = Modifier.align(Alignment.CenterHorizontally),
                                    imageVector = TablerIcons.Notebook,
                                    contentDescription = "Notes Tab"
                                )

                                Text(
                                    "Notes",
                                    fontSize = 12.sp,
                                    modifier = Modifier.align(Alignment.CenterHorizontally),
                                )
                            }
                        },
                        selected = state == 0,
                        onClick = { state = 0; NotesRouter.navigateTo(Screen.Notes) }
                    )

                    LeadingIconTab(
                        modifier = Modifier.tabHover(1),
                        text = {},
                        icon = {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(top = 4.dp, bottom = 4.dp)
                            )
                            {

                                @Composable
                                fun DeviceIcon(tint: Color = LocalContentColor.current.copy(alpha = LocalContentAlpha.current)) {
                                    Icon(
                                        modifier = Modifier.align(Alignment.CenterHorizontally),
                                        imageVector = TablerIcons.DeviceMobile,
                                        tint = tint,
                                        contentDescription = "Sync Notes Tab"
                                    )
                                }

                                if (isConnected)
                                    DeviceIcon(Color.Companion.Green)
                                else
                                    DeviceIcon()

                                Text(
                                    "Phone Notes", fontSize = 12.sp,
                                    modifier = Modifier.align(Alignment.CenterHorizontally)
                                )


                            }
                        },
                        selected = state == 1,
                        onClick = {
                            state = 1
                            viewModel.requestQRCode()
                            NotesRouter.navigateTo(Screen.Synced)
                        }
                    )

                    LeadingIconTab(
                        //TODO: find different better looking highlight method for archive
                        modifier = Modifier.align(Alignment.CenterHorizontally).tabHover(2),
                        text = { },
                        icon = {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {

                                val interactionSource = remember { MutableInteractionSource() }
                                val hoverDots by interactionSource.collectIsHoveredAsState()
                                Icon(
                                    modifier = Modifier.requiredSizeIn(12.dp).size(20.dp),
                                    imageVector = Octicons.Inbox24,
                                    contentDescription = "Archive Tab",
                                )
                                Icon(
                                    imageVector = TablerIcons.Dots,
                                    "asdf",
                                    modifier = Modifier
                                        .rotate(90f).requiredSizeIn(6.dp).size(15.dp)
                                        .align(Alignment.CenterEnd)
                                        .absoluteOffset(2.dp, -12.dp)
                                        .noRippleClickable(interactionSource = interactionSource) { //this modifier has  adds hoverable
                                            expandArchive = true
                                        },
                                    tint = if (hoverDots) MaterialTheme.colors.onPrimary else MaterialTheme.colors.onSecondary
                                )

                            }


                        },
                        selected = state == 2,
                        onClick = { state = 2; NotesRouter.navigateTo(Screen.Archive) },
                    )

                }
            }
        }
        Column {
            ArchiveDropDown(expandArchive, ::dismissArchiveDropDown)
        }
    }
}

@Composable
fun ArchiveDropDown(show: Boolean, onDismiss: ()->Unit) {
    val expanded by derivedStateOf { show }

    val fontSize = 12.sp
    val itemModifier = Modifier.padding(vertical = 0.dp, horizontal = 4.dp).heightIn(18.dp, 18.dp)
    DropdownMenu(
        offset = DpOffset(10.dp, 34.dp),
        expanded = expanded,
        onDismissRequest = {
            onDismiss()
        },
        modifier = Modifier.background(MaterialTheme.colors.surface)
    ) { //TODO: lookup a construct like withFontStyleProvider()
        DropdownMenuItem(
            modifier = itemModifier.align(Alignment.CenterHorizontally),
            contentPadding = PaddingValues(1.dp),
            onClick = { TODO("CLEAR ARCHIVE") }
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {

                Text(
                    "Empty Archive",
                    fontSize = fontSize
                )

                Spacer(modifier = Modifier.width(6.dp))
                Icon(
                    imageVector = Octicons.Trash24,
                    "Empty Archive",
                    modifier = Modifier.size(16.dp),
                )
            }
        }
    }
}

