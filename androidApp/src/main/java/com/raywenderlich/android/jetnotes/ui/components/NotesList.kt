package com.raywenderlich.android.jetnotes.ui.components

import android.provider.ContactsContract
import android.util.Log
import androidx.compose.foundation.background
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
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LiveData
import com.raywenderlich.jetnotes.domain.NoteProperty
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking


@ExperimentalMaterialApi
@Composable
fun NotesList(
    notes: LiveData<List<NoteProperty>>,
    onNoteCheckedChange: (NoteProperty) -> Unit,
    onEditNote : (NoteProperty) -> Unit,
    onRestoreNote: (NoteProperty) -> Unit = {},
    onArchiveNote: (NoteProperty) -> Unit = {},
    onDeleteNote: (NoteProperty) -> Unit = {},
    onSnackMessage: (String) -> Unit = {},
    isArchive: Boolean = false,
) {


    val listState = rememberLazyListState()
    val notes: List<NoteProperty> by notes
        .observeAsState(listOf())

    //TODO: sort notes by last Edited date instead?
    val notesReversed by derivedStateOf {notes.reversed()}

    LazyColumn(state = listState) {
        items(notesReversed, {note: NoteProperty -> note.id}) { note ->
            //var dismissOpacity by remember { mutableStateOf(0f)}
            var dismissState = rememberDismissState()
            if(dismissState.isDismissed(DismissDirection.StartToEnd)){
                onDeleteNote(note)
            }

            AnimatedSwipeDismiss(
                dismissState,
                item = note,
                background = { _ ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp)
                            .background(
                                MaterialTheme.colors.secondary,
                                shape = RoundedCornerShape(4.dp)
                            )
                    ) {
                        val alpha = 1f
                        Icon(
                            Icons.Filled.Delete,
                            contentDescription = "Delete",
                            modifier = Modifier
                                .align(Alignment.CenterStart)
                                .padding(start = 15.dp),
                            tint = MaterialTheme.colors.onSecondary
                        )
                    }
                },
                content = { dismissValue ->
                    Note(
                        note = note,
                        onEditNote = onEditNote,
                        onNoteCheckedChange = onNoteCheckedChange,
                        onRestoreNote = onRestoreNote,
                        onArchiveNote = onArchiveNote,
                        onDeleteNote = onDeleteNote,
                        isArchivedNote = isArchive,
                        onSnackMessage = onSnackMessage
                    )
                },
                onDismiss = { _ ->
                    //onDeleteNote(note)
                }
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