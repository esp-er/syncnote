package com.raywenderlich.android.jetnotes.ui.components
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*

import androidx.compose.runtime.*
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.raywenderlich.jetnotes.domain.NoteProperty
import com.raywenderlich.android.jetnotes.util.setClipboard
import com.raywenderlich.android.jetnotes.util.fromHex
import androidx.compose.foundation.BorderStroke
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.DpSize
import com.raywenderlich.android.jetnotes.domain.model.ColorModel
import com.raywenderlich.android.jetnotes.domain.model.NoteModel
import com.raywenderlich.jetnotes.domain.Util
import compose.icons.TablerIcons
import compose.icons.tablericons.Pin
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

@OptIn(ExperimentalFoundationApi::class)
@ExperimentalMaterialApi
@Composable
fun SyncedNote(
    note: NoteProperty,
    onCloneNote: (NoteProperty) -> Unit = {},
    onDeleteNote: (NoteProperty) -> Unit = {},
    onSnackMessage: (String) -> Unit = {},
    isArchivedNote: Boolean = false
){

    val expandedButtonsHeight = 32.dp

    val timeAgoText = remember { Util.timeAgoString(note.editDate, Clock.System.now())}

    var expandedState by rememberSaveable { mutableStateOf(false) }
    val expandedAnimatedDp by animateDpAsState(
        if(expandedState) expandedButtonsHeight else 0.dp ,
        animationSpec = tween(
            durationMillis = 125,
            easing = FastOutSlowInEasing
        ))
    val isFullyExpanded by derivedStateOf { expandedAnimatedDp == expandedButtonsHeight}

    val numLines = remember { note.content.lines().size + if(note.title.isBlank()) 0 else 1 }

    val backgroundShape = RoundedCornerShape(4.dp)
    Card(
        modifier = Modifier
            .fillMaxWidth()
            //.background(MaterialTheme.colors.surface, backgroundShape)
            .padding(8.dp),
        shape = backgroundShape,
        onClick = { expandedState = !expandedState },
        elevation = 4.dp
    ) {
        Column(horizontalAlignment = Alignment.End, modifier = Modifier.background(MaterialTheme.colors.surface)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(42.dp)
                    .background(MaterialTheme.colors.surface, backgroundShape)

                //.clickable(onClick = { onNoteClick(note) }) //note: make any node clickable with modifiers
            ) {
                NoteColor(
                    modifier = Modifier
                        .align(Alignment.Top)
                        .padding(top = 4.dp, start = 4.dp),
                    color = Color.fromHex(ColorModel.DEFAULT.hex), //TODO: fix coloring
                    size = 8.dp,
                    border = 0.5.dp
                )
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 10.dp, end = 8.dp, top = 8.dp, bottom = 6.dp)
                        .align(Alignment.Top)
                ) {
                    if (note.title.isNotBlank()) { //Alter layout when title blank
                        Text(
                            text = note.title,
                            color = MaterialTheme.colors.onPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            style = TextStyle(
                                fontWeight = FontWeight.Normal,
                                fontSize = 16.sp,
                                letterSpacing = 0.35.sp
                            )
                        )
                    }
                        Text(
                            modifier = Modifier.fillMaxWidth(),
                            text = note.content,
                            color = MaterialTheme.colors.onPrimary.copy(alpha = 0.8f),
                            overflow = TextOverflow.Ellipsis,
                            //TODO: Find a way to determine number of lines in Note?
                            maxLines = if (expandedState) 8 else if(note.title.isBlank()) 3 else minOf(numLines, 2),
                            style = TextStyle(
                                fontWeight = FontWeight.Normal,
                                fontSize = 14.sp,
                                letterSpacing = 0.25.sp
                            )
                        )
                }

                Column(horizontalAlignment = Alignment.End) {
                    if (!isArchivedNote && note.isPinned) // fix this conditional to a cleaner solution
                    {
                        Icon(
                            modifier = Modifier.size(20.dp).padding(2.dp),
                            imageVector = TablerIcons.Pin,
                            tint = MaterialTheme.colors.onSecondary,
                            contentDescription = "Restore Note Button"
                        )
                    }
                }

            }

            //TODO: line should be drawn below this box
            Box(modifier=Modifier.heightIn(14.dp,14.dp)){ //TODO: Separate card into text column and right-hand col properly
                Row(modifier = Modifier.padding(horizontal = 3.dp)){
                    Spacer(Modifier.weight(1f))
                    Text(timeAgoText, fontSize = 11.sp, style = TextStyle(MaterialTheme.colors.onSurface.copy(alpha=0.75f)))
                }
            }


            Box( modifier = Modifier.height(expandedAnimatedDp) ) {
                if(isFullyExpanded) {
                    Column(verticalArrangement = Arrangement.Center) {
                        Box(
                            modifier = Modifier.fillMaxWidth()
                                .height(0.7.dp)
                                .background(MaterialTheme.colors.primaryVariant)
                        )
                        SyncedNoteButtons(
                            note,
                            onDeleteNote = onDeleteNote,
                            onSnackMessage = onSnackMessage,
                        )
                    }
                }
            }

        }
    }
}





@Composable
fun SyncedNoteDropDownMenu(onDismiss: () -> Unit, onDelete: () -> Unit){
    var expanded by remember{ mutableStateOf(true )}
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false
            onDismiss() }
    ) {
        DropdownMenuItem(onClick = { onDelete() }) {
            Text("Delete Permanently")
        }
    }

}

@Preview
@Composable
fun SyncedNoteColorPreview() {
    NoteColor(Modifier.padding(4.dp), Color.Yellow, 40.dp, border = 1.dp)
}

@Composable
fun SyncedNoteButtons(
    note: NoteProperty,
    onDeleteNote: (NoteProperty) -> Unit = {},
    onSnackMessage: (String) -> Unit = {},
){

    var dropdownState by rememberSaveable{ mutableStateOf(false) }
    fun dismissDrop(){ dropdownState = false}
    fun deleteClicked() { onDeleteNote(note)
                          dismissDrop() }


    val ctx = LocalContext.current
    fun copyClicked(){
        setClipboard(ctx, note.content)
        onSnackMessage("Note text copied.")
    }


    Row(
        modifier = Modifier
            .padding(0.dp)
            .height(36.dp)
            .defaultMinSize(minHeight = 32.dp),
        verticalAlignment = Alignment.Bottom
    )
       {
           val buttonPadding = 2.dp
           IconButton( //Three dot dropdown button
               onClick = { dropdownState = true },
           ) {
               if(dropdownState) {
                   SyncedNoteDropDownMenu(::dismissDrop, ::deleteClicked)
               }
               Icon(
                   modifier = Modifier.padding(horizontal = 2.dp),
                   imageVector = Icons.Default.MoreVert,
                   tint = MaterialTheme.colors.onPrimary.copy(alpha=0.5f),
                   contentDescription = "Copy Note Button"
               )
           }
           Spacer(modifier = Modifier.weight(1f))


           IconButton( //"Copy" Button
               onClick = { copyClicked() },
               modifier = Modifier
                   //.align(Alignment.CenterVertically)
                   .padding(horizontal = buttonPadding)
           ) {
               Row(modifier = Modifier.align(Alignment.CenterVertically)){
                   Text("Copy",
                       modifier = Modifier.align(Alignment.CenterVertically),
                       fontSize = 12.sp)
                   Icon(
                       modifier = Modifier.padding(horizontal = 1.dp),
                       imageVector = Icons.Default.CopyAll,
                       tint = MaterialTheme.colors.onPrimary,
                       contentDescription = "Copy Note Button"
                   )
               }
           }

    }
}

@Preview(showBackground = true)
@Composable
fun PreviewSyncedNoteButtons(){
    NoteButtons(NoteProperty() , {}, {})
}