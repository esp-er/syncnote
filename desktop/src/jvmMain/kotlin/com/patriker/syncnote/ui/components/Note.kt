package com.patriker.syncnote.ui.components
import androidx.compose.animation.core.*
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.*
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
import com.patriker.syncnote.util.setClipboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.ui.awt.awtEventOrNull
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.isPrimaryPressed
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.window.Popup
import com.raywenderlich.jetnotes.domain.Util
import com.raywenderlich.jetnotes.routing.NotesRouter
import com.raywenderlich.jetnotes.routing.Screen
import compose.icons.TablerIcons
import compose.icons.Octicons
import compose.icons.octicons.Bookmark24
import compose.icons.octicons.BookmarkFill24
import compose.icons.octicons.BookmarkSlash24
import compose.icons.octicons.Copy24
import compose.icons.octicons.Pencil24
import compose.icons.octicons.Inbox24
import compose.icons.octicons.ArrowLeft24
import compose.icons.octicons.ArrowRight24
import compose.icons.octicons.Trash24
import compose.icons.tablericons.AlignJustified
import compose.icons.tablericons.Dots
import kotlinx.coroutines.delay
import kotlinx.datetime.Clock

@OptIn(androidx.compose.ui.ExperimentalComposeUiApi::class)
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

    val timeAgoText = remember { Util.timeAgoString(note.editDate, Clock.System.now())}

    val expandedButtonsHeight = 26.dp

    var expandedState by rememberSaveable { mutableStateOf(false) }
    val expandedAnimatedDp by animateDpAsState(
        if(expandedState) expandedButtonsHeight else 0.dp ,
        animationSpec = tween(
            durationMillis = 50,
            easing = FastOutSlowInEasing
        ))
    val isFullyExpanded by derivedStateOf { expandedAnimatedDp == expandedButtonsHeight}

    val titleIsBlank = note.title.isBlank()

    val maxLines = 8
    val maxLinesExpanded = 20
    val numLines = remember { note.content.lines().size + if(note.title.isBlank()) 0 else 1 }
    val maxContentLines = maxLines - if(!titleIsBlank) 1 else 0
    val maxContentLinesExpanded = maxLinesExpanded - if(!titleIsBlank) 1 else 0

    val backgroundShape = RoundedCornerShape(4.dp)
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()
    Card( //TODO: Make the expanded card scrollable with VerticalScrollBar if overflows
        modifier = Modifier
            .fillMaxWidth()
            .noRippleClickable(interactionSource = interactionSource){ //this modifier has  adds hoverable
                expandedState = !expandedState
            }
            .padding(horizontal = 6.dp, vertical = 4.dp),
        shape = backgroundShape,
        elevation = 4.dp
    ) {
        val cardBackground = if (isHovered) MaterialTheme.colors.primaryVariant.copy(alpha=0.1f) else MaterialTheme.colors.surface
        Column(modifier = Modifier.background(cardBackground, backgroundShape)){
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(32.dp)
                    .onPointerEvent(PointerEventType.Press) {
                        when {
                            it.buttons.isPrimaryPressed -> when (it.awtEventOrNull?.clickCount) {
                                2 -> onEditNote(note)
                                else -> { }
                            }
                        }
                    }
            ) {
                /*Column(modifier = Modifier.widthIn(24.dp,24.dp)) { TODO:fix coloring and reintroduce a badge for the card
                    NoteColor(
                        modifier = Modifier
                            .padding(top = 0.dp, start = 0.dp, bottom = 0.dp),
                        color = Color.fromHex("0xFFFFFF"), //TODO: fix coloring,
                        24.dp,
                        border = 0.8.dp
                    )
                }*/
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 10.dp, end = 8.dp, top = 8.dp, bottom = 0.dp)
                        .align(Alignment.CenterVertically)
                ) {
                    if (note.title.isNotBlank()) { //Alter layout when title blank
                        Text(
                            text = note.title,
                            color = MaterialTheme.colors.onPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Clip,
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
                        maxLines = if (expandedState) maxContentLinesExpanded else minOf(numLines, maxContentLines),
                        fontSize = 13.sp
                    )
                        //Spacer(Modifier.weight(1f))
                    if(!expandedState && numLines > maxLines){ //Vertical overflow case
                        Icon(imageVector = TablerIcons.Dots,
                            "Overflow dots",
                            modifier = Modifier.align(Alignment.CenterHorizontally).size(14.dp)
                        )
                    }
                    if(expandedState && numLines > maxLinesExpanded){
                        val diff = numLines - maxLinesExpanded
                        val lines = if(diff >  1) "lines" else "line"
                        Text("Read $diff more $lines...",
                            modifier = Modifier.clickable{ onEditNote(note) }
                                .align(Alignment.CenterHorizontally),
                            fontSize = 12.sp,
                            style = TextStyle(MaterialTheme.colors.onSecondary)
                        )
                    }
                }

                Column(horizontalAlignment = Alignment.End) {
                        if (!isArchivedNote && note.isPinned) //TODO: fix this conditional to a cleaner solution
                        {
                            Icon(
                                modifier = Modifier.size(18.dp).padding(2.dp),
                                imageVector = Octicons.BookmarkFill24,
                                tint = MaterialTheme.colors.onPrimary,
                                contentDescription = "Restore Note Button")
                        }
                }




                /*
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
                 */

            }

            Box(modifier=Modifier.heightIn(14.dp,14.dp)){ //TODO: Separate card into text column and right-hand col properly
                //TODO: fix rendering of this box(background clips)
                //TODO: create Utility function to calculate "hrs / days / weeks / months ago" for this timestamp
                Row(modifier = Modifier.padding(horizontal = 2.dp)){
                    Spacer(Modifier.weight(1f))
                    Text(timeAgoText, fontSize = 9.sp, style = TextStyle(MaterialTheme.colors.onSecondary))
                }
            }



            Box(modifier = Modifier.heightIn(expandedAnimatedDp, expandedAnimatedDp) ) {
                if(isFullyExpanded) {
                    Column(verticalArrangement = Arrangement.Center) {
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
                    .size(size)
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

@OptIn(ExperimentalFoundationApi::class)
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

    var copyClickedState by remember { mutableStateOf(false) }
    LaunchedEffect(copyClickedState){
        if (copyClickedState){
            delay(2000)
            copyClickedState = false
        }
    }


    fun copyClicked(){
        copyClickedState = true
        setClipboard(note.content)
    }

    Row(
        modifier = Modifier
            .padding(0.dp)
            .height(32.dp)
            .defaultMinSize(minHeight = 28.dp),
        verticalAlignment = Alignment.CenterVertically
    )
       {
           val buttonPadding = 2.dp
           val buttonSpacing = 12.dp
           val buttonSize = 14.dp
           val rightHandModifier = Modifier.padding(horizontal = 2.dp).size(buttonSize)


           if(!isArchive) {
               Icon(
                   modifier = Modifier
                       .clip(RoundedCornerShape(2.dp))
                       .align(Alignment.CenterVertically)
                       .padding(4.dp)
                       .size(buttonSize)
                       .clickable(interactionSource = remember { MutableInteractionSource() },
                       indication = rememberRipple(bounded = false, radius = buttonSize), // You can also change the color and radius of the ripple
                       onClick = { onTogglePin(note) }),
                   imageVector = if(note.isPinned) Octicons.BookmarkSlash24 else Octicons.Bookmark24,
                   tint = MaterialTheme.colors.onPrimary,
                   contentDescription = "Pin Note Button"
               )
               Spacer(modifier = Modifier.weight(1f))
               ArchiveButton(onArchiveNote = { onArchiveNote(note) }, buttonSize, rightHandModifier)
           }
           else{
               Spacer(modifier = Modifier.weight(1f))
               DeleteButton(note, onDeleteNote, buttonSize, rightHandModifier)
               Spacer(modifier= Modifier.width(buttonSpacing))
               UnArchiveButton({onRestoreNote(note)}, buttonSize, rightHandModifier)
           }

           Spacer(modifier= Modifier.width(buttonSpacing))
           /*TooltipArea( //TODO: Change this to use androidx.compose.ui.window.popup instead
               tooltip = {
                       Surface(
                           color = MaterialTheme.colors.primaryVariant,
                           shape = RoundedCornerShape(2.dp)
                       ) {

                           if(copyClickedState) {
                               Text(
                                   "Contents copied.",
                                   modifier = Modifier.padding(5.dp),
                                   fontSize = 10.sp
                               )
                           }
                       }
               },
               delayMillis = 1000
           ){
               NoteButton(onClick = {copyClicked()},
                   imageVector = Octicons.Copy24,
                   tint = MaterialTheme.colors.onPrimary,
                   iconSize =  buttonSize,
                   contentDescription = "Copy Note Button"
               )
           }*/

           NoteButton(onClick = {copyClicked()},
               imageVector = Octicons.Copy24,
               tint = MaterialTheme.colors.onPrimary,
               iconSize =  buttonSize,
               contentDescription = "Copy Note Button",
               content = {}
           )

           if(copyClickedState) {
               Box {
                   Popup(
                       alignment = Alignment.BottomCenter,
                       offset = IntOffset(-4, -8),
                       focusable = true, //Note: required for dismiss to work currently
                       onDismissRequest = {copyClickedState = false}
                   ){
                       Surface(color = MaterialTheme.colors.background, shape = RoundedCornerShape(2.dp)) {
                           //Clipboard utf-8 :"\uD83D\uDCCB",
                           Text(
                               text = "Copied!",
                               modifier = Modifier.padding(5.dp),
                               fontSize = 10.sp,
                           )
                       }
                   }
               }
           }

           Spacer(modifier= Modifier.width(buttonSpacing))
           NoteButton(onClick = { onEditNote(note) },
               imageVector = Octicons.Pencil24,
               tint = MaterialTheme.colors.onPrimary,
               iconSize = buttonSize,
               contentDescription = "Edit Note Button",
               content = {}
           )
           Spacer(modifier= Modifier.width(buttonSpacing))
           /*
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
           }*/


    }
}

@Preview
@Composable
fun NoteButton(onClick: () -> Unit, imageVector: ImageVector, iconSize: Dp, tint: Color, contentDescription: String = "",
    content: @Composable () -> Unit){
    return Icon(modifier = Modifier
        .clip(RoundedCornerShape(4.dp))
        .padding(horizontal = 4.dp, vertical = 4.dp)
        .size(iconSize)
        .clickable(interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(bounded = false, radius = iconSize), // You can also change the color and radius of the ripple
                onClick = onClick
                ),
        imageVector = imageVector,
        tint = tint,
        contentDescription = contentDescription
    )
}

@Preview
@Composable
fun NoteButton2(onClick: () -> Unit, imageVector: ImageVector, imageVector2: ImageVector, iconSize: Dp, tint: Color, contentDescription: String = ""){
    //TODO: Perhaps receive Modifier as an arg?
        return Row(modifier = Modifier
            .clip(RoundedCornerShape(2.dp))
            .padding(4.dp)
            .width(iconSize * 2).height(iconSize)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(
                    bounded = false,
                    radius = iconSize + (iconSize - 2.dp)
                ), // You can also change the color and radius of the ripple
                onClick = onClick
            )
        )
        {
            Icon(
                imageVector = imageVector,
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .size(iconSize-2.dp),
                tint = tint,
                contentDescription = contentDescription
            )
            Spacer(Modifier.width(2.dp))
            Icon(
                imageVector = imageVector2,
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .size(iconSize),
                tint = tint,
                contentDescription = contentDescription
            )
        }
}

@Preview
@Composable
fun UnArchiveButton(onRestoreNote: () -> Unit, buttonSize: Dp, modifier: Modifier) {
    NoteButton2(
            onClick = onRestoreNote,
            imageVector = Octicons.ArrowLeft24,
            imageVector2= Octicons.Inbox24,
            iconSize = buttonSize,
            tint = MaterialTheme.colors.onPrimary,
            contentDescription = "Archive Note Button"
        )
}

@Preview
@Composable
fun ArchiveButton(onArchiveNote: () -> Unit, buttonSize: Dp, modifier: Modifier) {
    NoteButton2(
        onClick = onArchiveNote,
        imageVector = Octicons.ArrowRight24,
        imageVector2 = Octicons.Inbox24,
        iconSize = buttonSize,
        tint = MaterialTheme.colors.onPrimary,
        ""
    )

}

@Preview
@Composable
fun DeleteButton(note: NoteProperty, onDeleteNote: (NoteProperty) -> Unit, buttonSize: Dp, modifier: Modifier) {

    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()

    fun buttonClicked() { onDeleteNote(note) }

    Box(modifier = Modifier.hoverable(interactionSource)){
        NoteButton(onClick = ::buttonClicked,
            imageVector = Octicons.Trash24,
            iconSize = buttonSize,
            tint = if(isHovered) MaterialTheme.colors.error else MaterialTheme.colors.onPrimary,
            "Delete Note Button",
            content = {}
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