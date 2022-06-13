package com.raywenderlich.android.jetnotes.ui.components

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.LibraryBooks
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.raywenderlich.jetnotes.routing.NotesRouter
import com.raywenderlich.jetnotes.routing.Screen


@Composable
fun TopTabBar(initState: Int, isConnected: Boolean = false) {

    var state by remember{ mutableStateOf(initState) }

    val textStyle = TextStyle(color = MaterialTheme.colors.onSecondary, fontSize = 12.sp)
    Column {
        CustomTabRow(modifier = Modifier.padding(horizontal = 8.dp), selectedTabIndex = state, tabWeights = listOf(3f,3f,1f)) { //TODO: Find better way to pass in tab weights
            LeadingIconTab(
                text = {}, //TODO: fix text colors e.g primary / on prim, etc
                icon = {
                    val selectedMod = if(state == 0){ //TODO: How can this be done with currying / passing func instead of val
                        Modifier.fillMaxSize(1f)
                            .background(MaterialTheme.colors.primaryVariant)
                    }else {
                        Modifier.fillMaxSize(1f)
                    }

                    Column(modifier = Modifier.fillMaxWidth().padding(top=4.dp,bottom=4.dp)) {
                        Icon(
                            modifier = Modifier.align(Alignment.CenterHorizontally),
                            imageVector = Icons.Default.LibraryBooks,
                            tint = MaterialTheme.colors.onSecondary,
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
                            .padding(top=4.dp,bottom=4.dp)
                    )
                    {


                        Icon(
                            modifier = Modifier.align(Alignment.CenterHorizontally),
                            imageVector = Icons.Default.Devices,
                            tint = if (isConnected) Color.Companion.Green else MaterialTheme.colors.onSecondary,
                            contentDescription = "Sync Notes Tab"
                        )
                        Text(
                            "Desktop Notes", fontSize = 12.sp,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )


                    }
                },
                selected = state == 1,
                onClick = { state = 1; NotesRouter.navigateTo(Screen.Sync) }
            )

            LeadingIconTab( //TODO: find different better looking highlight method for archive
                text = { },
                icon = {
                    val selectedMod = if (state == 2) {
                        Modifier.fillMaxSize(1f)
                            .background(MaterialTheme.colors.primaryVariant)
                    } else {
                        Modifier.fillMaxSize(1f)
                    }

                    Column(modifier = Modifier.fillMaxSize()) {
                        Icon(
                            modifier = Modifier.padding(top = 12.dp),
                            imageVector = Icons.Default.Archive,
                            contentDescription = "Archive Tab",
                            tint = MaterialTheme.colors.onSecondary
                        )
                    }
                },
                selected = state == 2,
                onClick = { state = 2; NotesRouter.navigateTo(Screen.Archive) },
            )
        }
    }
}
