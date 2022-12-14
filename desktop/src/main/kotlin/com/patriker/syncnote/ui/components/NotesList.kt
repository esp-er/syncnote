package com.patriker.syncnote.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.flow.StateFlow
import com.patriker.syncnote.domain.NoteProperty
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
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
    expandAllTrigger: Boolean = false,
    isArchive: Boolean = false,
) {
    Box(
        modifier = Modifier.fillMaxSize().padding(top=4.dp),
    ) {
        val notesList: List<NoteProperty> by notes.collectAsState()

        val notesSorted by derivedStateOf {
            notesList.sortedWith( compareBy<NoteProperty> {!(it.isPinned)}.thenByDescending { it.editDate} )
        }


        val expandNotes by derivedStateOf { expandAllTrigger }
        val corScope = rememberCoroutineScope()
        val listState = rememberLazyListState()

        if(notesList.isEmpty()){
            Column(modifier = Modifier.align(Alignment.Center)) {
                if (isArchive) {
                    Text(
                        "Archive Empty!",
                        style = TextStyle(color = MaterialTheme.colors.onBackground, 14.sp)
                    )
                } else {
                    Text(
                        "Click +New or Enter CTRL + N to create a note.",
                        style = TextStyle(color = MaterialTheme.colors.onBackground, 14.sp)
                    )

                }
            }
        }


        var prevDelta by remember { mutableStateOf(0f) }
        LazyColumn(state = listState, modifier = Modifier
            .padding(end = 7.dp)
            .draggable(rememberDraggableState { delta ->
                corScope.launch {

                    val deriv = if(prevDelta +1 < delta) -2
                                else if(prevDelta - 1 > delta) 2
                                else if(prevDelta < delta) -1
                                else if(prevDelta > delta) 1
                                else 0
                                prevDelta = delta

                    listState.scrollBy(-delta)

                    when(deriv) {
                        -2 -> listState.animateScrollBy(8 * -delta)
                        2 -> listState.animateScrollBy(8 * -delta)
                        -1 -> listState.animateScrollBy(2 * -delta)
                        1 -> listState.animateScrollBy(2 * -delta)
                        else -> listState.animateScrollBy(-delta)
                    }
                }
            }, orientation = Orientation.Vertical)

        ) {
            items(notesSorted, { note: NoteProperty -> note.id }) { note ->
                //var dismissOpacity by remember { mutableStateOf(0f)}
                Note(
                    note = note,
                    onEditNote = onEditNote,
                    onNoteCheckedChange = onNoteCheckedChange,
                    onRestoreNote = onRestoreNote,
                    onArchiveNote = onArchiveNote,
                    onDeleteNote = onDeleteNote,
                    onTogglePin = onTogglePin,
                    expandAllTrigger = expandNotes,
                    isArchivedNote = isArchive
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