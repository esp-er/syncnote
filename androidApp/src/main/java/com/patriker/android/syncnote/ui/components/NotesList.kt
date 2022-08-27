package com.patriker.android.syncnote.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Archive
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.LiveData
import com.patriker.syncnote.domain.NoteProperty
import compose.icons.TablerIcons
import compose.icons.tablericons.SquarePlus


@ExperimentalMaterialApi
@Composable
fun NotesList(
    notes: LiveData<List<NoteProperty>>,
    onNoteCheckedChange: (NoteProperty) -> Unit,
    onEditNote : (NoteProperty) -> Unit,
    onRestoreNote: (NoteProperty) -> Unit = {},
    onArchiveNote: (NoteProperty) -> Unit = {},
    onDeleteNote: (NoteProperty) -> Unit = {},
    onTogglePin: (NoteProperty) -> Unit = {},
    onSnackMessage: (String) -> Unit = {},
    isArchive: Boolean = false,
) {


    val listState = rememberLazyListState()
    val notes: List<NoteProperty> by notes
        .observeAsState(listOf())

    val notesSorted by derivedStateOf {
        notes.sortedWith( compareBy<NoteProperty> {!(it.isPinned)}.thenByDescending { it.editDate} )
    }


    if(notes.isEmpty()){
        Column(modifier = Modifier.padding(top=48.dp).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            if (isArchive) {
                Text(
                    "Archive is Empty.",
                    style = TextStyle(color = MaterialTheme.colors.onBackground, 14.sp),
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            } else {
                Row(modifier = Modifier.align(Alignment.CenterHorizontally)){
                    Text(
                        "Press",
                        style = TextStyle(color = MaterialTheme.colors.onBackground, 14.sp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Icon(
                        imageVector = TablerIcons.SquarePlus,
                        contentDescription = "Plus Icon",
                        modifier = Modifier
                            .size(16.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        "New",
                        style = TextStyle(color = MaterialTheme.colors.onBackground, 14.sp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "to create a note.",
                        style = TextStyle(color = MaterialTheme.colors.onBackground, 14.sp)
                    )
                }

            }
        }
    }

    LazyColumn(state = listState) {

        //TODO: list pinned notes on top!
        items(notesSorted,  { note: NoteProperty -> note.id}) { note ->
            //var dismissOpacity by remember { mutableStateOf(0f)}
            var dismissState = rememberDismissState()
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