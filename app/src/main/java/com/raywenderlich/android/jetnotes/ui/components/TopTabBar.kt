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
import com.raywenderlich.android.jetnotes.routing.JetNotesRouter
import com.raywenderlich.android.jetnotes.routing.Screen


@Composable
fun TopTabBar(initState: Int) {

    var state by remember{ mutableStateOf(initState) }
    fun archiveClick() {
        state = 1
        JetNotesRouter.navigateTo(Screen.Archive)
    }
    val textStyle = TextStyle(color = MaterialTheme.colors.onSecondary)
    Column {
        CustomTabRow(selectedTabIndex = state, tabWeights = listOf(4f,1f)) { //TODO: Find better way to pass in tab weights
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
                onClick = { state = 0; JetNotesRouter.navigateTo(Screen.Notes) }
            )

            LeadingIconTab(
                text = { },
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
