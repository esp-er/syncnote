package com.patriker.syncnote.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.outlined.Archive
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.raywenderlich.jetnotes.domain.NoteProperty
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.forEach

/*
@ExperimentalMaterialApi
@Composable
fun SwipeNotesList(
    notes: List<NoteProperty>,
    onNoteCheckedChange: (NoteProperty) -> Unit,
    onEditNote : (NoteProperty) -> Unit,
    onRestoreNote: (NoteProperty) -> Unit = {},
    onArchiveNote: (NoteProperty) -> Unit = {},
    onDeleteNote: (NoteProperty) -> Unit = {},
    onTogglePin: (NoteProperty) -> Unit = {},
    onSnackMessage: (String) -> Unit = {},
    isArchive: Boolean = false,
) {


    //val listState = rememberLazyListState()
    //val notes: List<NoteProperty> by notes
    //.observeAsState(listOf())


    //TODO: sort notes by last Edited date instead?
    val notesReversed by derivedStateOf {
        notes.reversed().sortedBy { !(it.isPinned) }
    }

    Column{

        //TODO: list pinned notes on top!
        //items(notesReversed,  { note: NoteProperty -> note.id}) { note ->
        notesReversed.forEach{ note ->
            //var dismissOpacity by remember { mutableStateOf(0f)}
            val dismissState = rememberDismissState()
            if(dismissState.isDismissed(DismissDirection.StartToEnd)){
                onArchiveNote(note)
            }

            AnimatedSwipeDismiss(
                dismissState,
                item = note,
                background = { _ ->
                    val backColor = if(isArchive) MaterialTheme.colors.error
                    else MaterialTheme.colors.secondaryVariant
                    Box(
                        contentAlignment = Alignment.CenterStart,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(8.dp)
                            .background(backColor, shape = RoundedCornerShape(4.dp))
                    ) {
                        Row(verticalAlignment = Alignment.Bottom){
                            val actionText = if (isArchive) "Delete" else "Archive"
                            Icon(
                                if (isArchive) Icons.Outlined.Delete else Icons.Outlined.Archive,
                                contentDescription = "$actionText Note",
                                modifier = Modifier.align(Alignment.CenterVertically)
                                    .padding(start = 15.dp),
                                tint = MaterialTheme.colors.onPrimary
                            )
                            Spacer(modifier = Modifier.width(20.dp))
                            Text("$actionText")
                        }
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
                        onTogglePin = onTogglePin,
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
*/

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


    //val listState = rememberLazyListState()
    //val notes: List<NoteProperty> by notes
    //.observeAsState(listOf())

    val notesList: List<NoteProperty> by notes.collectAsState()


    //TODO: sort notes by last Edited date instead?
    val notesReversed by derivedStateOf {
        notes.value.reversed().sortedBy { !(it.isPinned) }
    }

    Column{

        //TODO: list pinned notes on top!
        //items(notesReversed,  { note: NoteProperty -> note.id}) { note ->
        notesList.forEach{ note ->
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