package com.patriker.syncnote.ui.components
import androidx.compose.animation.core.*
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.*
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*

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
import com.patriker.syncnote.util.fromHex
import com.patriker.syncnote.ui.noRippleClickable
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import compose.icons.TablerIcons
import compose.icons.Octicons
import compose.icons.octicons.*
import compose.icons.tablericons.AlignJustified
import compose.icons.tablericons.Pin
import compose.icons.tablericons.PinnedOff
import kotlin.math.exp

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

    val expandedButtonsHeight = 24.dp

    var expandedState by rememberSaveable { mutableStateOf(false) }
    val expandedAnimatedDp by animateDpAsState(
        if(expandedState) expandedButtonsHeight else 0.dp ,
        animationSpec = tween(
            durationMillis = 50,
            easing = FastOutSlowInEasing
        ))
    val isFullyExpanded by derivedStateOf { expandedAnimatedDp == expandedButtonsHeight}

    val numLines = remember { note.content.lines().size + if(note.title.isBlank()) 0 else 1 }

    val backgroundShape = RoundedCornerShape(4.dp)
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .noRippleClickable(interactionSource = interactionSource){
                expandedState = !expandedState
            }
            //.hoverable(interactionSource = interactionSource)
            //.background(MaterialTheme.colors.surface, backgroundShape)
            //.noRippleClickable { expandedState = !expandedState }
            .padding(horizontal = 6.dp, vertical = 4.dp),
        shape = backgroundShape,
        elevation = 4.dp
    ) {
        val cardBackground = if (isHovered) MaterialTheme.colors.primaryVariant.copy(alpha=0.1f) else MaterialTheme.colors.surface
        Column(horizontalAlignment = Alignment.End){
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(42.dp)
                    .background(cardBackground, backgroundShape)
                //.clickable(onClick = { onNoteClick(note) }) //note: make any node clickable with modifiers
            ) {
                NoteColor(
                    modifier = Modifier
                        .align(Alignment.Top)
                        .padding(top = 12.dp, start = 10.dp, bottom = 12.dp),
                    color = Color.fromHex("0xFFFFFF"), //TODO: fix coloring
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
                            fontSize = 13.sp,
                            fontWeight = FontWeight.W500
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                    }
                        Text(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 1.dp),
                            text = note.content,
                            color = MaterialTheme.colors.onPrimary.copy(alpha = 0.8f),
                            overflow = TextOverflow.Ellipsis,
                            //TODO: Find a way to determine number of lines in Note?
                            maxLines = if (expandedState) 8 else if(note.title.isBlank()) 3 else minOf(numLines, 2),
                            fontSize = 13.sp
                        )
                }

                if(!isArchivedNote && note.isPinned) //TODO: fix this conditional to a cleaner solution
                {
                   Icon(
                        modifier = Modifier.size(18.dp).padding(2.dp),
                        imageVector = Octicons.BookmarkFill24,
                        tint = MaterialTheme.colors.onPrimary,
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

                }
            }

            Box(modifier = Modifier.height(expandedAnimatedDp) ) {
                if(isFullyExpanded) {
                    Column {
                        Box(modifier = Modifier.fillMaxWidth()
                            .height(0.5.dp)
                            .background(MaterialTheme.colors.primaryVariant)) //Divider Line
                        NoteButtons(
                            note,
                            onEditNote = onEditNote,
                            onArchiveNote = onArchiveNote,
                            onRestoreNote = onRestoreNote,
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
                imageVector = TablerIcons.AlignJustified,
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
    onRestoreNote: (NoteProperty) -> Unit = {},
    onDeleteNote: (NoteProperty) -> Unit = {},
    onTogglePin: (NoteProperty) -> Unit = {},
    onSnackMessage: (String) -> Unit = {},
    isArchive: Boolean = false
){


    fun copyClicked(){
        onSnackMessage("Note text copied.")
    }

    Row(
        modifier = Modifier
            .padding(0.dp)
            .height(32.dp)
            .defaultMinSize(minHeight = 28.dp),
        verticalAlignment = Alignment.Bottom
    )
       {
           val buttonPadding = 2.dp
           val buttonSize = 14.dp
           val rightHandModifier = Modifier.padding(horizontal = 2.dp).size(buttonSize)

           if(!isArchive) {
               IconButton( //Pin Button
                   onClick = { onTogglePin(note) }
               ) {
                   Icon(
                       modifier = Modifier.padding(horizontal = 2.dp).size(buttonSize),
                       imageVector = if(note.isPinned) Octicons.BookmarkSlash24 else Octicons.Bookmark24,
                       tint = MaterialTheme.colors.onPrimary,
                       contentDescription = "Pin Note Button"
                   )
               }
               Spacer(modifier = Modifier.weight(1f))
               ArchiveButton(onArchiveNote = { onArchiveNote(note) }, rightHandModifier)
           }
           else{
               Spacer(modifier = Modifier.weight(1f))
               DeleteButton(note, onDeleteNote, rightHandModifier)
               UnArchiveButton({onRestoreNote(note)}, rightHandModifier)
           }

           IconButton( //"Copy" Button
               onClick = { copyClicked() },
               modifier = Modifier
                   //.align(Alignment.CenterVertically)
                   .padding(horizontal = buttonPadding)
           ) {
               Row(modifier = Modifier.align(Alignment.CenterVertically)){
                   Icon(
                       modifier = rightHandModifier,
                       imageVector = Octicons.Copy24,
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
                       modifier = rightHandModifier,
                       imageVector = Octicons.Pencil24,
                       tint = MaterialTheme.colors.onPrimary,
                       contentDescription = "Edit Note Button"
                   )
               }

        }

    }
}

@Preview
@Composable
fun UnArchiveButton(onRestoreNote: () -> Unit, modifier: Modifier) {
    IconButton(
        onClick = { onRestoreNote() },
        modifier = Modifier
            //.align(Alignment.CenterVertically)
            .padding(horizontal = 2.dp)
    ) {
        Row{
            Icon(
                modifier = Modifier.size(12.dp),
                imageVector = Octicons.ArrowLeft24,
                tint = MaterialTheme.colors.onPrimary,
                contentDescription = "Archive Note Button"
            )
            Icon(
                modifier = modifier,
                imageVector = Octicons.Inbox24,
                tint = MaterialTheme.colors.onPrimary,
                contentDescription = "Archive Note Button"
            )
        }
    }

}

@Preview
@Composable
fun ArchiveButton(onArchiveNote: () -> Unit, modifier: Modifier) {
    IconButton(
        onClick = { onArchiveNote() },
        modifier = Modifier
        //.align(Alignment.CenterVertically)
        .padding(horizontal = 2.dp)
    ) {
        Row{
            Icon(
                modifier = Modifier.size(12.dp),
                imageVector = Octicons.ArrowRight24,
                tint = MaterialTheme.colors.onPrimary,
                contentDescription = "Archive Note Button"
            )
            Icon(
                modifier = modifier,
                imageVector = Octicons.Inbox24,
                tint = MaterialTheme.colors.onPrimary,
                contentDescription = "Archive Note Button"
            )
        }
    }

}

@Preview
@Composable
fun DeleteButton(note: NoteProperty, onDeleteNote: (NoteProperty) -> Unit, modifier: Modifier) {
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()
    IconButton(
        onClick = { onDeleteNote(note) },
        modifier = Modifier.hoverable(interactionSource)
            //.align(Alignment.CenterVertically)
            .padding(horizontal = 2.dp)
    ) {
        Icon(
            modifier = modifier,
            imageVector = Octicons.Trash24,
            tint = if(isHovered) MaterialTheme.colors.error else MaterialTheme.colors.onPrimary,
            contentDescription = "Archive Note Button"
        )
    }

}

@Preview
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