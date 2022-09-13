package com.patriker.android.syncnote.ui.components

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.lifecycle.LiveData
import com.patriker.syncnote.domain.NoteProperty


@ExperimentalMaterialApi
@Composable
fun SyncedNoteList(
    notes: LiveData<List<NoteProperty>>,
    onCloneNote: (NoteProperty) -> Unit = {},
    onDeleteNote: (NoteProperty) -> Unit = {},
    onSnackMessage: (String) -> Unit = {},
) {


    val listState = rememberLazyListState()
    val notes: List<NoteProperty> by notes
        .observeAsState(listOf())

    val notesReversed by derivedStateOf {notes.reversed()}

    LazyColumn(state = listState) {
        items(notesReversed, {note: NoteProperty -> note.id}) { note ->
            //var dismissOpacity by remember { mutableStateOf(0f)}
            var dismissState = rememberDismissState()
            if(dismissState.isDismissed(DismissDirection.StartToEnd)){
                onDeleteNote(note)
            }
            SyncedNote(
                note = note,
                onCloneNote = onCloneNote,
                onSnackMessage = onSnackMessage,
            )

        }
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