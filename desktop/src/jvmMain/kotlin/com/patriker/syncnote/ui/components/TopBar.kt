package com.patriker.syncnote.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import compose.icons.Octicons
import compose.icons.TablerIcons
import compose.icons.octicons.Plus24
import compose.icons.octicons.ThreeBars16
import compose.icons.tablericons.SquarePlus
import kotlinx.coroutines.launch

@Composable
fun TopBar(onClickNew: () -> Unit){
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
                        modifier = Modifier.width(24.dp).weight(1f)) { //content
                IconButton(
                    modifier = Modifier.align(Alignment.End),
                    onClick = {
                    }
                ) {
                    Icon(
                        imageVector = Octicons.ThreeBars16,
                        contentDescription = "Drawer Button"
                    )
                }
            }
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