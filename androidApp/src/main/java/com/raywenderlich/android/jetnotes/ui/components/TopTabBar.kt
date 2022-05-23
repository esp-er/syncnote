package com.raywenderlich.android.jetnotes.ui.components

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.LibraryBooks
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import com.raywenderlich.jetnotes.routing.NotesRouter
import com.raywenderlich.jetnotes.routing.Screen


@Composable
fun TopTabBar(initState: Int) {

    var state by remember{ mutableStateOf(initState) }
    fun archiveClick() {
        state = 1
        NotesRouter.navigateTo(Screen.Archive)
    }
    val textStyle = TextStyle(color = MaterialTheme.colors.onSecondary, fontSize = 14.sp)
    Column {
        CustomTabRow(selectedTabIndex = state, tabWeights = listOf(5f,2f)) { //TODO: Find better way to pass in tab weights
            LeadingIconTab(
                text = { Text("Notes", style = textStyle)}, //TODO: fix text colors e.g primary / on prim, etc
                icon = {
                    Icon(
                        imageVector = Icons.Default.LibraryBooks,
                        tint = MaterialTheme.colors.onSecondary,
                        contentDescription = "Notes Tab"
                    )
                },
                selected = state == 0,
                onClick = { state = 0; NotesRouter.navigateTo(Screen.Notes) }
            )

            LeadingIconTab(
                text = { Text("Archive", style = textStyle)},
                icon = {
                        Icon(
                            imageVector = Icons.Default.Archive,
                            contentDescription = "Archive Tab",
                            tint = MaterialTheme.colors.onSecondary
                        )
                    },
                selected = state == 1,
                onClick = { archiveClick() },
            )
        }
    }
}
