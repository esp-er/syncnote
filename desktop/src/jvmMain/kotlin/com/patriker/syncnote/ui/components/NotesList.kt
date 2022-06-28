package com.patriker.syncnote.ui.components

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.StateFlow
import com.raywenderlich.jetnotes.domain.NoteProperty

@ExperimentalMaterialApi
@Composable
fun NotesList(
    notes: StateFlow<List<NoteProperty>>,
    onNoteCheckedChange: (NoteProperty) -> Unit,
    onEditNote : (NoteProperty) -> Unit,
    onRestoreNote: (NoteProperty) -> Unit = {},
    onArchiveNote: (NoteProperty) -> Unit = {},
    onDeleteNote: (NoteProperty) -> Unit = {},
    onTogglePin: (NoteProperty) -> Unit = {},
    onSnackMessage: (String) -> Unit = {},
    isArchive: Boolean = false,
) {
    Box(
        modifier = Modifier.fillMaxSize().padding(top=4.dp)
    ) {
        val notesList: List<NoteProperty> by notes.collectAsState()

        //TODO: sort notes by last Edited date instead?
        val notesReversed by derivedStateOf {
            notesList.reversed().sortedBy { !(it.isPinned) }
        }

        val listState = rememberLazyListState()
        LazyColumn(state = listState, modifier = Modifier.padding(end = 6.dp)) {
            //TODO: Figure out automatic padding for scollbar?

            //TODO: list pinned notes on top!
            items(notesReversed, { note: NoteProperty -> note.id }) { note ->
                //var dismissOpacity by remember { mutableStateOf(0f)}
                Note(
                    note = note,
                    onEditNote = onEditNote,
                    onNoteCheckedChange = onNoteCheckedChange,
                    onRestoreNote = onRestoreNote,
                    onArchiveNote = onArchiveNote,
                    onDeleteNote = onDeleteNote,
                    onTogglePin = onTogglePin,
                    isArchivedNote = isArchive,
                    onSnackMessage = onSnackMessage
                )
            }
        }
        VerticalScrollbar(
            modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight().padding(vertical = 4.dp, horizontal = 2.dp),
            adapter = rememberScrollbarAdapter(
                scrollState = listState
            )
        )
    }
}

/*
@ExperimentalMaterialApi
@Preview
@Composable
private fun NotesListPreview() {
    NotesList(
        notes = listOf(
            NoteProperty(1, "Note 1", "Content 1", null),
            NoteProperty(2, "Note 2", "Content 2", false),
            NoteProperty(3, "Note 3", "Content 3", true)
        ),
        onNoteCheckedChange = {},
        onEditNote = {},
        onRestoreNote = {}
    )
}
*/