package com.raywenderlich.android.jetnotes.ui.components
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
import androidx.compose.ui.platform.LocalContext
import com.raywenderlich.android.jetnotes.domain.model.ColorModel
import compose.icons.TablerIcons
import compose.icons.tablericons.Copy
import compose.icons.tablericons.Edit
import compose.icons.tablericons.Pin
import compose.icons.tablericons.PinnedOff

@OptIn(ExperimentalFoundationApi::class)
@ExperimentalMaterialApi
@Composable
fun Note(
    note: NoteProperty,
    onEditNote: (NoteProperty) -> Unit = {},
    onNoteCheckedChange: (NoteProperty) -> Unit = {},
    onRestoreNote: (NoteProperty) -> Unit  = {},
    onArchiveNote: (NoteProperty) -> Unit = {},
    onDeleteNote: (NoteProperty) -> Unit = {},
    onTogglePin: (NoteProperty) -> Unit = {},
    onSnackMessage: (String) -> Unit = {},
    isArchivedNote: Boolean = false
){

    val expandedButtonsHeight = 32.dp

    var expandedState by rememberSaveable { mutableStateOf(false) }
    val expandedAnimatedDp by animateDpAsState(
        if(expandedState) expandedButtonsHeight else 0.dp ,
        animationSpec = tween(
            durationMillis = 100,
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
        val lineColor = if(expandedState) MaterialTheme.colors.onPrimary.copy(alpha=0.7f) else MaterialTheme.colors.onPrimary.copy(alpha=0.0f)
        Column(horizontalAlignment = Alignment.End, modifier = Modifier.background(MaterialTheme.colors.surface)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(42.dp)
                    .background(MaterialTheme.colors.surface, backgroundShape)
                    .drawBehind {
                        drawLine(
                            lineColor,
                            Offset(0f, size.height),
                            Offset(size.width, size.height),
                            0.9f
                        )
                    }
                //.clickable(onClick = { onNoteClick(note) }) //note: make any node clickable with modifiers
            ) {
                NoteColor(
                    modifier = Modifier
                        .align(Alignment.Top)
                        .padding(top = 12.dp, start = 10.dp, bottom = 12.dp),
                    color = Color.fromHex(ColorModel.DEFAULT.hex), //TODO: fix coloring
                    24.dp,
                    border = 0.8.dp
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
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 2.dp),
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

                if(!isArchivedNote && note.isPinned) // fix this conditional to a cleaner solution
                {
                   Icon(
                        modifier = Modifier.size(20.dp).padding(2.dp),
                        imageVector = TablerIcons.Pin,
                        tint = MaterialTheme.colors.onSecondary,
                        contentDescription = "Restore Note Button"
                    )
                }

                Column {

                    //Could use a better abstraction
                    //than null for "no checkbox"
                    if (note.canBeChecked) {
                        Checkbox(
                            checked = note.isChecked,
                            onCheckedChange = { checkedState ->
                                val newNote = note.copy(isChecked = checkedState)
                                onNoteCheckedChange(newNote)
                                //note: see how the state is copied and passed on in an new obj!
                            },
                            modifier = Modifier
                                .padding(start = 8.dp)
                        )
                    }

                    if (isArchivedNote) {
                        IconButton(
                            onClick = { onRestoreNote(note) },
                        ) {
                            Icon(
                                imageVector = Icons.Default.Unarchive,
                                tint = MaterialTheme.colors.onPrimary,
                                contentDescription = "Restore Note Button"
                            )

                        }
                    }
                }
            }

            Box(modifier = Modifier.height(expandedAnimatedDp) ) {
                if(isFullyExpanded) {
                    NoteButtons(
                        note,
                        onEditNote = onEditNote,
                        onArchiveNote = onArchiveNote,
                        onDeleteNote = onDeleteNote,
                        onTogglePin = onTogglePin,
                        onSnackMessage = onSnackMessage,
                        isArchive = isArchivedNote
                    )
                }
            }

        }
    }
}




@Composable
fun NoteColor(
    modifier: Modifier = Modifier,
    color: Color,
    size: Dp,
    border: Dp
) {
    val shape = RoundedCornerShape(corner = CornerSize(4.dp))
    Box(
        modifier = modifier
            .size(size)
            .clip(shape) //must set shape before background
            .background(color)
            .border(BorderStroke(border, Color.DarkGray), shape),
    ) {
        CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
            Icon(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(0.dp)
                    .matchParentSize(),
                imageVector = Icons.Outlined.Notes,
                tint = Color.Black.copy(alpha = 0.4f),
                contentDescription = "Restore Note Button"
            )
            /*
        Icon(
            painter = painterResource(
                id = R.drawable.ic_baseline_color_lens_24
            ),
            contentDescription = "Open Color Picker Button",
            tint = MaterialTheme.colors.onPrimary
        )*/
        }
    }
}

@Composable
fun NoteDropDownMenu(onDismiss: () -> Unit, onDelete: () -> Unit){
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
fun NoteColorPreview() {
    NoteColor(Modifier.padding(4.dp), Color.Yellow, 40.dp, border = 1.dp)
}

@Composable
fun NoteButtons(
    note: NoteProperty,
    onEditNote: (NoteProperty) -> Unit,
    onArchiveNote: (NoteProperty) -> Unit = {},
    onDeleteNote: (NoteProperty) -> Unit = {},
    onTogglePin: (NoteProperty) -> Unit = {},
    onSnackMessage: (String) -> Unit = {},
    isArchive: Boolean = false
){ //TODO: change tint of buttons to lighter /resp / darker, tune size

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
           IconButton( //Pin Button
                onClick = { onTogglePin(note) }
           ) {
               if(dropdownState) {
                   NoteDropDownMenu(::dismissDrop, ::deleteClicked)
               }
               Icon(
                   modifier = Modifier.padding(horizontal = 2.dp),
                   imageVector = if(note.isPinned) TablerIcons.PinnedOff else TablerIcons.Pin,
                   tint = MaterialTheme.colors.onPrimary,
                   contentDescription = "Pin Note Button"
               )
           }
           Spacer(modifier = Modifier.weight(1f))

           if(!isArchive) {
               IconButton(
                   onClick = { onArchiveNote(note) },
                   modifier = Modifier
                       //.align(Alignment.CenterVertically)
                       .padding(horizontal = buttonPadding)
               ) {
                   Row(modifier = Modifier.align(Alignment.CenterVertically)) {
                       Icon(
                           modifier = Modifier.padding(horizontal = 4.dp),
                           imageVector = Icons.Outlined.Archive,
                           tint = MaterialTheme.colors.onPrimary,
                           contentDescription = "Archive Note Button"
                       )
                   }
               }
           }

           IconButton( //"Copy" Button
               onClick = { copyClicked() },
               modifier = Modifier
                   //.align(Alignment.CenterVertically)
                   .padding(horizontal = buttonPadding)
           ) {
               Row(modifier = Modifier.align(Alignment.CenterVertically)){
                   Icon(
                       modifier = Modifier.padding(horizontal = 4.dp),
                       imageVector = TablerIcons.Copy,
                       tint = MaterialTheme.colors.onPrimary,
                       contentDescription = "Copy Note Button"
                   )
               }
           }


           IconButton(
            onClick = { onEditNote(note) },
            modifier = Modifier
                //.align(Alignment.CenterVertically)
                .padding(horizontal = buttonPadding)
        ) {
               Row(modifier = Modifier.align(Alignment.CenterVertically)) {
                   Icon(
                       modifier = Modifier.padding(horizontal = 4.dp),
                       imageVector = TablerIcons.Edit,
                       tint = MaterialTheme.colors.onPrimary,
                       contentDescription = "Edit Note Button"
                   )
               }

        }

    }
}

@Preview(showBackground = true)
@Composable
fun PreviewNoteButtons(){
    NoteButtons(NoteProperty() , {}, {})
}

/* TODO: optional: fix previews
@ExperimentalMaterialApi
@Preview
@Composable
private fun NotePreview(){ //Note "private"
    val test = NoteProperty(
            id = 12345,
            title = "Note One",
            content = """Text One asdf asdf asdf asdf asdf asdf asdf asdf asdf asdf
                |Text One asdf asdf asdf asdf asdf asdf asdf asdf asdf asdf
                |Text One asdf asdf asdf asdf asdf asdf asdf asdf asdf asdf
                |Text One asdf asdf asdf asdf asdf asdf asdf asdf asdf asdf
                |Text One asdf asdf asdf asdf asdf asdf asdf asdf asdf asdf
                """.trimMargin("|"),
            isCheckedOff = false
        )
    Note(test, {}, {})
}*/