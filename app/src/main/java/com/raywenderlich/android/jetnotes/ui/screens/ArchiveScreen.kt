package com.raywenderlich.android.jetnotes.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.raywenderlich.android.jetnotes.domain.model.NoteModel
import com.raywenderlich.android.jetnotes.routing.JetNotesRouter
import com.raywenderlich.android.jetnotes.routing.Screen
import com.raywenderlich.android.jetnotes.ui.components.AppDrawer
import com.raywenderlich.android.jetnotes.ui.components.NotesList
import com.raywenderlich.android.jetnotes.ui.components.TopAppBar
import com.raywenderlich.android.jetnotes.ui.components.TopTabBar
import com.raywenderlich.android.jetnotes.viewmodel.MainViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@ExperimentalMaterialApi
@Composable
fun ArchiveScreen(viewModel: MainViewModel) {
    /*TODO: Perhaps consider merging this screen with note screen? for smooth tab transition*/

    //this delegate unwraps State<List<NoteModel>> into regular List<NoteModel>
    val trashedNotes: List<NoteModel> by viewModel
        .notesInTrash
        .observeAsState(listOf()) //Model (Observer model state)

    val scaffoldState = rememberScaffoldState() //remembers drawer and snackbar state
    val coroutineScope = rememberCoroutineScope()

    fun showSnackBar(message: String) {
        coroutineScope.launch{
            val showbar = launch {
                scaffoldState.snackbarHostState.showSnackbar(message, duration = SnackbarDuration.Indefinite)
            }
            delay(2000) //Trick to allow shorter snackbar time
            showbar.cancel()
        }
    }


    BackHandler(
        onBack = {
            if (scaffoldState.drawerState.isOpen) { //comment: useful back behavior pattern with the drawer
                coroutineScope.launch { scaffoldState.drawerState.close() }
            } else {
                JetNotesRouter.navigateTo(Screen.Notes)
            }
        }
    )

    Scaffold (
        topBar =
        {
            Column {
                TopAppBar(
                    modifier = Modifier.heightIn(50.dp,50.dp),
                    backgroundColor = MaterialTheme.colors.secondaryVariant,
                    title = {
                        Text(
                            text = "Open Notes",
                            fontSize = 18.sp
                        )
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                coroutineScope.launch {
                                    scaffoldState.drawerState.open()
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Filled.List,
                                contentDescription = "Drawer Button"
                            )
                        }
                    }
                )
                TopTabBar(initState = 1) //Tabs
            }
        },
        scaffoldState = scaffoldState, //lets the scaffold display the correct state
        snackbarHost = {scaffoldState.snackbarHostState},
        drawerContent = {
            AppDrawer(
                currentScreen = Screen.Archive,
                closeDrawerAction = {
                    //Drawer close
                    coroutineScope.launch{
                        scaffoldState.drawerState.close()
                    }
                }
            )
        },
        content = {
            /*if (trashedNotes.isNotEmpty()) {
                NotesList( // here
                    notes = trashedNotes,
                    onNoteCheckedChange = { viewModel.onNoteCheckedChange(it) },
                    onEditNote = { viewModel.onNoteClick(it) },
                    onRestoreNote =  { viewModel.restoreNoteFromArchive(it) },
                    onDeleteNote = { viewModel.permaDeleteNote(it) },
                    isArchive = true,
                    onSnackMessage = ::showSnackBar
                )
            }*/
        }
    )
}
