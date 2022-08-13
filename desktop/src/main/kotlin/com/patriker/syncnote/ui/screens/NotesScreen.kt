package com.patriker.syncnote.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.SnackbarDefaults.backgroundColor
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
//import androidx.compose.runtime.livedata.observeAsState
import com.raywenderlich.jetnotes.routing.Screen

//import com.raywenderlich.android.jetnotes.ui.components.TopAppBar
import com.patriker.syncnote.ui.components.*
import com.raywenderlich.jetnotes.MainViewModel
import compose.icons.TablerIcons
import compose.icons.tablericons.SquarePlus
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt




@ExperimentalMaterialApi
@Composable
fun NotesScreen(viewModel: MainViewModel) {

    //val configuration = LocalConfiguration.current


    var expandAllTrigger by remember { mutableStateOf(false) }

    val isConnected = viewModel.isSyncing //TODO: change to observable (reactive) value
    Box(modifier = Modifier.background(MaterialTheme.colors.background)) {
        Column {
            TopBar(viewModel::onCreateNewNoteClick,  { expandAllTrigger = !expandAllTrigger})
            //horLineSeparator()
            TopTabBar(initState = 0, viewModel, onClearArchive = viewModel::clearArchive) //Tabs
            NotesList( // here
                notes = viewModel.notes,
                onNoteCheckedChange = { viewModel.onNoteCheckedChange(it) },
                onEditNote = { viewModel.onNoteClick(it) },
                onRestoreNote = { viewModel.restoreNoteFromArchive(it) },
                onArchiveNote = { viewModel.archiveNote(it) },
                onDeleteNote = { viewModel.permaDeleteNote(it) },
                onTogglePin = { viewModel.togglePin(it) },
                expandAllTrigger = expandAllTrigger,
                isArchive = false,
            )
        }
    }

}
