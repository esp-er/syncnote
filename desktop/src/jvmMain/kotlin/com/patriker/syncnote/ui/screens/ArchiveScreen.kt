package com.patriker.syncnote.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.patriker.syncnote.ui.components.*
import com.raywenderlich.jetnotes.routing.Screen
import com.raywenderlich.jetnotes.MainViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@ExperimentalMaterialApi
@Composable
fun ArchiveScreen(viewModel: MainViewModel) {
    /*TODO: Perhaps consider merging this screen with note screen? for smooth tab transition*/

    val isConnected = viewModel.isSyncing


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


    Scaffold (
        topBar =
        {
            Column {
                TopAppBar(
                    modifier = Modifier.heightIn(50.dp,50.dp),
                    backgroundColor = MaterialTheme.colors.background,
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
                                imageVector = Icons.Filled.Menu,
                                contentDescription = "Drawer Button"
                            )
                        }
                    }
                )
                //horLineSeparator()
                TopTabBar(initState = 2) //Tabs
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

                },
                isConnected = isConnected
            )
        },
        //drawerShape = CustomDrawerShape(drawerWidth, drawerHeight),
        content = {
            NotesList( // here
                notes = viewModel.notesInArchive,
                onNoteCheckedChange = { viewModel.onNoteCheckedChange(it) },
                onEditNote = { viewModel.onNoteClick(it) },
                onRestoreNote =  { viewModel.restoreNoteFromArchive(it) },
                onDeleteNote = { viewModel.permaDeleteNote(it) },
                //onPinNote = {viewModel.pinNote(it)},
                isArchive = true,
                onSnackMessage = ::showSnackBar
            )
        }
    )
}
