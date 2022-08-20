package com.patriker.syncnote.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.isCtrlPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
//import androidx.compose.runtime.livedata.observeAsState

//import com.raywenderlich.android.jetnotes.ui.components.TopAppBar
import com.patriker.syncnote.ui.components.*
import com.patriker.syncnote.MainViewModel


@ExperimentalMaterialApi
@Composable
fun NotesScreen(viewModel: MainViewModel) {

    //val configuration = LocalConfiguration.current


    var expandAllTrigger by remember { mutableStateOf(false) }

    val isConnected = viewModel.isSyncing //TODO: change to observable (reactive) value

    val showExpandAllButton by derivedStateOf { viewModel.notes.value.isNotEmpty() }
    Box(modifier = Modifier.background(MaterialTheme.colors.background)) {
        Column {
            TopBar(viewModel::onCreateNewNoteClick,  { expandAllTrigger = !expandAllTrigger}, showExpandAllButton)
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
