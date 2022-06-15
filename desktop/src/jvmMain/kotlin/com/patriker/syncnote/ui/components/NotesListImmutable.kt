package com.patriker.syncnote.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.raywenderlich.jetnotes.domain.NoteProperty
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking


@ExperimentalMaterialApi
@Composable
fun SyncedNoteList(
    notes: List<NoteProperty>,
    onCloneNote: (NoteProperty) -> Unit = {},
    onDeleteNote: (NoteProperty) -> Unit = {},
    onSnackMessage: (String) -> Unit = {},
) {


    //val listState = rememberLazyListState()
    /*val notes: List<NoteProperty> by notes
        .observeAsState(listOf())*/

    //TODO: sort notes by last Edited date instead?
    val notesReversed by derivedStateOf {notes.reversed()}

    //LazyColumn(state = listState) {
    Column{
        //items(notesReversed, {note: NoteProperty -> note.id}) { note ->
        notesReversed.forEach{ note ->
            //var dismissOpacity by remember { mutableStateOf(0f)}
            var dismissState = rememberDismissState()
            if(dismissState.isDismissed(DismissDirection.StartToEnd)){
                onDeleteNote(note)
            }
            SyncedNote(
                note = note,
                onCloneNote = onCloneNote,
                onSnackMessage = onSnackMessage
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