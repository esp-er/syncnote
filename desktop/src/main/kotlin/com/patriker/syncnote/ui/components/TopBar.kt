package com.patriker.syncnote.ui.components

import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpOffset
import com.patriker.syncnote.ui.ThemeSettingsDesktop
import com.patriker.syncnote.routing.NotesRouter
import com.patriker.syncnote.routing.Screen
import compose.icons.Octicons
import compose.icons.TablerIcons
import compose.icons.octicons.FoldDown24
import compose.icons.octicons.FoldUp24
import compose.icons.octicons.Inbox24
import compose.icons.octicons.Plus24
import compose.icons.octicons.ThreeBars16
import compose.icons.tablericons.DeviceMobile
import compose.icons.tablericons.Refresh
import compose.icons.tablericons.Moon
import compose.icons.tablericons.Notebook

@Composable
fun TopBar(onClickNew: () -> Unit, onToggleExpand: () -> Unit, showExpandNotes: Boolean = true){
    var showMenu by remember { mutableStateOf(false) }
    fun toggleMenu() { showMenu = !showMenu}
    var expandIcon by remember { mutableStateOf(Octicons.FoldDown24)}
    fun toggleExpandIcon(){
        expandIcon =
            when {
                expandIcon == Octicons.FoldDown24 -> Octicons.FoldUp24
                else -> Octicons.FoldDown24
            }
    }

    TopAppBar(
        backgroundColor = MaterialTheme.colors.background,
        elevation = 0.dp,
        modifier= Modifier.fillMaxWidth().heightIn(40.dp,40.dp)) {


        Row(Modifier.height(36.dp).padding(horizontal = 4.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically) {
            //Left side Icon
            Column(horizontalAlignment = Alignment.Start, modifier = Modifier.padding(vertical = 4.dp).requiredWidthIn(min=80.dp)){
                NewButton(onClickNew)

            }


            //Title
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f).requiredWidthIn(min=80.dp)){
                //ProvideTextStyle(value = MaterialTheme.typography.h6) {
                Text(
                    textAlign = TextAlign.Start,
                    maxLines = 1,
                    color = MaterialTheme.colors.onSurface,
                    text = "SyncNote",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.W500
                )
                //}
            }

            Column(horizontalAlignment = Alignment.End){
                Row(Modifier
                    .requiredWidthIn(min=40.dp, max=100.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End){

                    //Spacer(Modifier.fillMaxWidth())
                    Column(
                        modifier = Modifier.requiredWidthIn(min = 40.dp).padding(horizontal = 4.dp)
                    ) { //content

                        if(showExpandNotes) {
                            Box(modifier = Modifier.requiredWidthIn(max=40.dp)
                                .border(BorderStroke(1.dp, color = MaterialTheme.colors.primaryVariant), shape = RoundedCornerShape(4.dp))
                            ){
                                Icon(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .clickable(interactionSource = remember { MutableInteractionSource() },
                                            indication = rememberRipple(
                                                bounded = false,
                                                radius = 20.dp
                                            ), // You can also change the color and radius of the ripple
                                            onClick = { toggleExpandIcon(); onToggleExpand() })
                                        .fillMaxWidth(),
                                    //.border(BorderStroke(1.dp, color = MaterialTheme.colors.primaryVariant)),
                                    imageVector = expandIcon,
                                    contentDescription = "Toggle expanded notes button",

                                    )
                            }

                        }
                    }


                    Column(
                        modifier = Modifier.requiredWidthIn(min=30.dp).padding(horizontal = 8.dp)) { //content
                        Icon(
                            modifier = Modifier
                                .clickable(interactionSource = remember { MutableInteractionSource() },
                                    indication = rememberRipple(bounded = false, radius = 16.dp), // You can also change the color and radius of the ripple
                                    onClick = { if(!showMenu) toggleMenu()}),
                            imageVector = Octicons.ThreeBars16,


                            contentDescription = "Drawer Button"
                        )

                    }
                    Column(horizontalAlignment = Alignment.End) {
                        MainDropDown(showMenu, ::toggleMenu)
                    }
                }
            }


        }
    }
}


@Composable
fun MainDropDown(show: Boolean, onDismiss: () -> Unit) {
    val expanded by derivedStateOf { show }

    val fontSize = 13.sp
    val itemModifier = Modifier.padding(vertical = 0.dp, horizontal = 4.dp).heightIn(28.dp,28.dp)
    DropdownMenu(
        offset = DpOffset(0.dp, 16.dp),
        expanded = expanded,
        onDismissRequest = {
            onDismiss()
        },
        modifier = Modifier.background(MaterialTheme.colors.surface)

    ) { //TODO: lookup a construct like withFontStyleProvider()
        DropdownMenuItem(
            modifier = itemModifier,
            contentPadding = PaddingValues(2.dp),
            onClick = {
                NotesRouter.navigateTo(Screen.Notes)
            }) {
            Row(verticalAlignment = Alignment.CenterVertically){
                Icon(
                    imageVector = TablerIcons.Notebook,
                    "Notes Screen",
                    modifier = Modifier.size(16.dp),
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    "Notes",
                    fontSize = fontSize
                )
            }
        }
        DropdownMenuItem(
            modifier = itemModifier,
            contentPadding = PaddingValues(2.dp),
            onClick = {
                NotesRouter.navigateTo(Screen.Synced)
            }) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                imageVector = TablerIcons.DeviceMobile,
    "Notes Screen",
                    modifier = Modifier.size(16.dp),
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("Phone Notes", fontSize = fontSize)
            }
        }
        DropdownMenuItem(
            modifier = itemModifier,
            contentPadding = PaddingValues(2.dp),
            onClick = {
                NotesRouter.navigateTo(Screen.Archive)
            }) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Octicons.Inbox24,
                    "Notes Screen",
                    modifier = Modifier.size(16.dp),
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("Archive", fontSize = fontSize)
            }
        }
        DropdownMenuItem(
            modifier = itemModifier,
            contentPadding = PaddingValues(2.dp),
            onClick = {

            }) {
            Spacer(modifier = Modifier.width(16.dp))
            ToggleDark(ThemeSettingsDesktop.isUserDefined, ThemeSettingsDesktop.isDarkThemeEnabled)
        }

        DropdownMenuItem(
            modifier = itemModifier,
            contentPadding = PaddingValues(2.dp),
            onClick = {

            }) {
            Spacer(modifier = Modifier.width(16.dp))
            ToggleSync()
        }
    }
}
@Composable
private fun ToggleSync(modifier: Modifier = Modifier) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {

        Column(modifier = Modifier.align(Alignment.CenterVertically).weight(2f)) {
            Row {
                Text(
                    text = "Sync on Local Network",
                    fontSize = 13.sp,
                    //style = MaterialTheme.typography.body2,
                    //color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f),
                    //.align(alignment = Alignment.Start)
                )

                Spacer(modifier = Modifier.width(6.dp))
                Icon(
                    imageVector = TablerIcons.Refresh,
                    "Sync Icon",
                    modifier = Modifier
                        .size(16.dp)
                        .align(Alignment.CenterVertically)
                )
            }
        }

        Column(modifier = Modifier.align(Alignment.CenterVertically).weight(1f)) {
            Checkbox(
            checked = false,
            onCheckedChange = { },
            modifier = Modifier
                .padding(start = 4.dp)
                .align(alignment = Alignment.End)
            )

        }
    }
}


@Composable
private fun ToggleDark(userDefined: Boolean = false, isEnabled:Boolean) {
    Row(verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Column(modifier = Modifier.align(Alignment.CenterVertically).weight(2f)){
            Row{
                Text(
                    text = "Dark mode",
                    fontSize = 13.sp,
                    //style = MaterialTheme.typography.body2,
                    //color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f),
                )
                Spacer(modifier = Modifier.width(6.dp))
                Icon(
                    imageVector = TablerIcons.Moon,
                    "Night Icon",
                    modifier = Modifier
                        .size(16.dp)
                        .align(Alignment.CenterVertically)
                )
            }
        }
        Column (modifier = Modifier.align(Alignment.CenterVertically).weight(1f)){
            val initialState = isSystemInDarkTheme()
            var darkEnabled by remember { mutableStateOf(if(userDefined) isEnabled else initialState ) }
            Checkbox(
                checked = darkEnabled,
                onCheckedChange = {
                    it.also { state ->
                        darkEnabled = state
                        ThemeSettingsDesktop.isDarkThemeEnabled = state
                        ThemeSettingsDesktop.isUserDefined = true
                    }
                },
                modifier = Modifier
                    .padding(start = 2.dp)
                    .align(alignment = Alignment.End)
                    .fillMaxHeight(0.5f)
            )
        }
    }
}

@Composable
fun NewButton(buttonClicked: () -> Unit){
   Button(
       shape = RoundedCornerShape(4.dp),
       contentPadding = PaddingValues(4.dp),
       modifier = Modifier.heightIn(32.dp).widthIn(32.dp, 80.dp).defaultMinSize(1.dp,1.dp),
       onClick = { buttonClicked() }
   ) {
       Row(modifier = Modifier.padding(horizontal = 2.dp)){
           Icon(
               imageVector = Octicons.Plus24,
               contentDescription = "Add Note Button",
               tint = Color.White,
               modifier = Modifier
                   .align(Alignment.Bottom)
                   .size(24.dp)
           )
           //Spacer(modifier = Modifier.padding(horizontal = 2.dp))
           Text("New",
               fontSize = 13.sp,
               modifier = Modifier.align(Alignment.CenterVertically),
               color = Color.White
           )
       }
   }
}