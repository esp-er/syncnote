package com.patriker.android.syncnote.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Archive
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.patriker.syncnote.routing.NotesRouter
import com.patriker.syncnote.routing.Screen
import compose.icons.TablerIcons
import compose.icons.tablericons.DeviceLaptop
import compose.icons.tablericons.Notebook


@Composable
fun TopTabBar(initState: Int, isConnected: Boolean = false) {

    var state by remember{ mutableStateOf(initState) }

    val textStyle = TextStyle(color = MaterialTheme.colors.onSecondary, fontSize = 12.sp)
    Column {
        CustomTabRow(modifier = Modifier.padding(horizontal = 8.dp),
            selectedTabIndex = state,
            tabWeights = listOf(3f,3f,1f),
            indicatorShape = RoundedCornerShape(2.dp)
        ) { //TODO: Find better way to pass in tab weights
            LeadingIconTab(
                text = {}, //TODO: fix text colors e.g primary / on prim, etc
                icon = {
                    val selectedMod = if(state == 0){ //TODO: How can this be done with currying / passing func instead of val
                        Modifier
                            .fillMaxSize(1f)
                            .background(MaterialTheme.colors.primaryVariant)
                    }else {
                        Modifier.fillMaxSize(1f)
                    }

                    Column(modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp, bottom = 4.dp)) {
                        Icon(
                            modifier = Modifier.align(Alignment.CenterHorizontally),
                            imageVector = TablerIcons.Notebook,
                            contentDescription = "Notes Tab"
                        )

                        Text("Notes", fontSize = 12.sp, modifier = Modifier.align(Alignment.CenterHorizontally),
                        )
                    }
                },
                selected = state == 0,
                onClick = { state = 0; NotesRouter.navigateTo(Screen.Notes) }
            )

            LeadingIconTab(
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
                                imageVector = TablerIcons.DeviceLaptop,
                                tint = tint,
                                contentDescription = "Sync Notes Tab"
                            )
                        }

                        if(isConnected)
                            DeviceIcon(Color.Green)
                        else
                            DeviceIcon()

                        Text(
                            "Desktop Notes", fontSize = 12.sp,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )


                    }
                },
                selected = state == 1,
                onClick = { state = 1; NotesRouter.navigateTo(Screen.Synced) }
            )

            LeadingIconTab( //TODO: find different better looking highlight method for archive
                text = { },
                icon = {
                    val selectedMod = if (state == 2) {
                        Modifier
                            .fillMaxSize(1f)
                            .background(MaterialTheme.colors.primaryVariant)
                    } else {
                        Modifier.fillMaxSize(1f)
                    }

                    Column(modifier = Modifier.fillMaxSize()) {
                        Icon(
                            modifier = Modifier.padding(top = 12.dp),
                            imageVector = Icons.Outlined.Archive,
                            contentDescription = "Archive Tab",
                        )
                    }
                },
                selected = state == 2,
                onClick = { state = 2; NotesRouter.navigateTo(Screen.Archive) },
            )
        }
    }
}
