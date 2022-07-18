package com.patriker.syncnote.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.material.Text
import androidx.compose.ui.unit.dp
import com.raywenderlich.jetnotes.routing.NotesRouter
import com.raywenderlich.jetnotes.routing.Screen

//import com.raywenderlich.android.jetnotes.ui.components.TopAppBar
import androidx.compose.material.TopAppBar
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.sp
import com.patriker.syncnote.ui.components.*
import com.raywenderlich.jetnotes.MainViewModel
import compose.icons.Octicons
import compose.icons.octicons.ThreeBars16
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch



@ExperimentalMaterialApi
@Composable
fun SyncScreen(viewModel: MainViewModel, isHost: Boolean = false) {


    val isConnected: Boolean = viewModel.isSyncing
    val syncingHost: String = "archlinux"

    //this delegate unwraps State<List<NoteModel>> into regular List<NoteModel>
    val scaffoldState = rememberScaffoldState() //remembers drawer and snackbar state
    val coroutineScope = rememberCoroutineScope()
    Box(modifier = Modifier.background(MaterialTheme.colors.background).fillMaxSize()) {
        Column {
            TopBar(viewModel::onCreateNewNoteClick, {})
            //horLineSeparator()
            TopTabBar(initState = 1) //Tabs

            Box(modifier = Modifier.fillMaxSize(0.75f).align(Alignment.CenterHorizontally)) {
                Column{
                    if (!viewModel.isPaired) {
                        HostPairWidget(viewModel.qrBitmapFlow, viewModel.pairingInfoFlow, {})
                    } else {
                        Text("test")
                    }
                }
            }
        }
    }
}
