package com.patriker.syncnote.ui.components
import androidx.compose.animation.core.*
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.*
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.patriker.syncnote.domain.NoteProperty
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.material.*
import com.patriker.syncnote.ui.noRippleClickable
import com.patriker.syncnote.domain.Util
import compose.icons.Octicons
import compose.icons.TablerIcons
import compose.icons.octicons.BookmarkFill24
import compose.icons.octicons.Copy24
import compose.icons.tablericons.Dots
import kotlinx.datetime.Clock

@ExperimentalMaterialApi
@Composable
fun SyncedNote(
    note: NoteProperty,
    onCloneNote: (NoteProperty) -> Unit = {},
    onSnackMessage: (String) -> Unit = {},
    expandAllTrigger: Boolean = false
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
    LaunchedEffect(expandAllTrigger){
        expandedState = expandAllTrigger
    }
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

    Card(
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
                //.clickable(onClick = { onNoteClick(note) }) //note: make any node clickable with modifiers
            ) {
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
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                    }
                    Text(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 1.dp),
                        text = note.content,
                        color = MaterialTheme.colors.onPrimary.copy(alpha=0.8f),
                        overflow = TextOverflow.Ellipsis,
                        maxLines = if (expandedState) maxContentLinesExpanded else minOf(numLines, maxContentLines),
                        fontSize = 13.sp,
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
                            modifier = Modifier.clickable{ //onEditNote(note)
                            }
                                .align(Alignment.CenterHorizontally),
                            fontSize = 12.sp,
                            style = TextStyle(MaterialTheme.colors.onSecondary)
                        )
                    }
                }
                Column(horizontalAlignment = Alignment.End) {
                    if (note.isPinned)
                    {
                        Icon(
                            modifier = Modifier.size(18.dp).padding(2.dp),
                            imageVector = Octicons.BookmarkFill24,
                            tint = MaterialTheme.colors.onPrimary,
                            contentDescription = "Restore Note Button")
                    }
                }



            }
            Box(modifier=Modifier.heightIn(14.dp,14.dp)){ //TODO: Separate card into text column and right-hand col properly
                Row(modifier = Modifier.padding(horizontal = 2.dp)){
                    Spacer(Modifier.weight(1f))
                    Text(timeAgoText, fontSize = 9.sp, style = TextStyle(MaterialTheme.colors.onSecondary))
                }
            }

            //Note Color highlight
            /*
            Box(modifier = Modifier.heightIn(4.dp).fillMaxWidth().
                background(MaterialTheme.colors.primary)
                .clip(RoundedCornerShape(8.dp))
            ) {
                Spacer(Modifier.fillMaxSize())
            }*/

            Box(modifier = Modifier.heightIn(expandedAnimatedDp, expandedAnimatedDp)
            ) {
            if(isFullyExpanded) {
                    Column(verticalArrangement = Arrangement.Center) {
                        Box(modifier = Modifier.fillMaxWidth()
                            .height(0.5.dp)
                            .background(MaterialTheme.colors.primaryVariant)) //Divider Line
                        SyncedNoteButtons(
                            note,
                            onSnackMessage = {},
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
    onSnackMessage: (String) -> Unit = {},
){

    var dropdownState by rememberSaveable{ mutableStateOf(false) }
    fun dismissDrop(){ dropdownState = false}

    fun copyClicked(){
        //setClipboard(ctx, note.content)
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
               /*
               if(dropdownState) {
                   SyncedNoteDropDownMenu(::dismissDrop, ::deleteClicked)
               }
               Icon(
                   modifier = Modifier.padding(horizontal = 2.dp),
                   imageVector = Icons.Default.MoreVert,
                   tint = MaterialTheme.colors.onPrimary.copy(alpha=0.5f),
                   contentDescription = "Copy Note Button"
               )*/
           }
           Spacer(modifier = Modifier.weight(1f))


           IconButton( //"Copy" Button
               onClick = { copyClicked() },
               modifier = Modifier
                   //.align(Alignment.CenterVertically)
                   .padding(horizontal = buttonPadding)
           ) {

               Icon(
                   modifier = Modifier.padding(horizontal = 1.dp),
                   imageVector = Octicons.Copy24,
                   tint = MaterialTheme.colors.onPrimary,
                   contentDescription = "Copy Note Button"
               )
           }

    }
}

@Preview
@Composable
fun PreviewSyncedNoteButtons(){
    NoteButtons(NoteProperty() , {}, {})
}