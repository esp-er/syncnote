package com.patriker.syncnote.ui.components

import androidx.compose.foundation.clickable
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
import com.raywenderlich.jetnotes.routing.NotesRouter
import com.raywenderlich.jetnotes.routing.Screen
import com.raywenderlich.jetnotes.theme.ThemeSettings
import compose.icons.Octicons
import compose.icons.TablerIcons
import compose.icons.octicons.Inbox24
import compose.icons.octicons.Plus24
import compose.icons.octicons.ThreeBars16
import compose.icons.tablericons.*
import kotlinx.coroutines.launch

@Composable
fun TopBar(onClickNew: () -> Unit){
    var showMenu by remember { mutableStateOf(false) }
    fun toggleMenu() { showMenu = !showMenu}

    TopAppBar(
        backgroundColor = Color.Transparent,
        elevation = 0.dp,
        modifier= Modifier.fillMaxWidth().heightIn(40.dp,40.dp)) {


        Row(Modifier.height(36.dp).padding(horizontal = 4.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically) {
            //Left side Icon
            Column(horizontalAlignment = Alignment.Start, modifier = Modifier.weight(1f).padding(vertical = 4.dp)){

//                Text("NEW")
                NewButton(onClickNew)
               /* TODO: "NEW" Button here
                IconButton(
                    onClick = { },
                    enabled = true,
                ) {
                    Icon(
                        imageVector = Octicons.ThreeBars16,
                        contentDescription = "Back",
                    )
                }*/
            }

            //Title
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)){
                //ProvideTextStyle(value = MaterialTheme.typography.h6) {
                        Text(
                            textAlign = TextAlign.Center,
                            maxLines = 1,
                            text = "SyncNote",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.W500,
                            color = MaterialTheme.colors.onBackground
                        )
                //}
            }

            Column(horizontalAlignment = Alignment.End,
                        modifier = Modifier.width(30.dp).padding(horizontal = 4.dp).weight(1f)) { //content

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
        }
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
                NotesRouter.navigateTo(Screen.Sync)
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
            ToggleDark()
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
            Switch( //TODO: implement toggling for sync
                checked = false,
                onCheckedChange = { },
                modifier = Modifier
                    .padding(start = 8.dp)
                    .align(alignment = Alignment.End)
            )
        }
    }
}


@Composable
private fun ToggleDark() {
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
            Switch(
                checked = ThemeSettings.isDarkThemeEnabled,
                onCheckedChange = { ThemeSettings.isDarkThemeEnabled = it },
                modifier = Modifier
                    .padding(start = 8.dp)
                    .align(alignment = Alignment.End)
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
               contentDescription = "Add Note Button" ,
               modifier = Modifier
                   .align(Alignment.Bottom)
                   .size(24.dp)
           )
           //Spacer(modifier = Modifier.padding(horizontal = 2.dp))
           Text("New",
               fontSize = 13.sp,
               modifier = Modifier.align(Alignment.CenterVertically)
           )
       }
   }
}