package com.patriker.syncnote.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
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
                TopBar(viewModel::onCreateNewNoteClick)
                //horLineSeparator()
                TopTabBar(initState = 2) //Tabs
            }
        },
        scaffoldState = scaffoldState, //lets the scaffold display the correct state
        snackbarHost = {scaffoldState.snackbarHostState},
        drawerContent = {},
        drawerGesturesEnabled = false,
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
