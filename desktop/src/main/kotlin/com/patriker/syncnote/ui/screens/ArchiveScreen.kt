package com.patriker.syncnote.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.patriker.syncnote.ui.components.*
import com.patriker.syncnote.MainViewModel

@ExperimentalMaterialApi
@Composable
fun ArchiveScreen(viewModel: MainViewModel) {
    /*TODO: Perhaps consider merging this screen with note screen? for smooth tab transition*/

    var expandAllTrigger by remember { mutableStateOf(false) }

    Box(modifier = Modifier.background(MaterialTheme.colors.background)) {
        Column {
            TopBar(viewModel::onCreateNewNoteClick, { expandAllTrigger = !expandAllTrigger})
            //horLineSeparator()
            TopTabBar(initState = 2, viewModel, onClearArchive = viewModel::clearArchive) //Tabs
            NotesList( // here
                notes = viewModel.notesInArchive,
                onNoteCheckedChange = { viewModel.onNoteCheckedChange(it) },
                onEditNote = { viewModel.onNoteClick(it) },
                onRestoreNote = { viewModel.restoreNoteFromArchive(it) },
                onDeleteNote = { viewModel.permaDeleteNote(it) },
                expandAllTrigger = expandAllTrigger,
                //onPinNote = {viewModel.pinNote(it)},
                isArchive = true,
            )
        }
    }
}
