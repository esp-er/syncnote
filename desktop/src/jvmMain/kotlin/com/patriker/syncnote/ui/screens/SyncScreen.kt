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

import androidx.compose.ui.Alignment
import com.patriker.syncnote.ui.components.*
import com.raywenderlich.jetnotes.MainViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext


@ExperimentalMaterialApi
@Composable
fun SyncScreen(viewModel: MainViewModel, isHost: Boolean = false) {

    //TODO: implement signals to start listening when on this screen
    //Add popup for when client sends PairingData
    //Popup can only appear on this screen

    val isConnected = viewModel.isSyncing.collectAsState()
    val devicePaired = viewModel.isPaired.collectAsState()
    val syncingHost: String = "archlinux"

    fun onAcceptPairing(deviceName: String) {
        viewModel.hostAcceptedPairing()
    }


    //this delegate unwraps State<List<NoteModel>> into regular List<NoteModel>
    //val coroutineScope = rememberCoroutineScope()
    Box(modifier = Modifier.background(MaterialTheme.colors.background).fillMaxSize()) {
        Column {
            TopBar(viewModel::onCreateNewNoteClick, {})
            //horLineSeparator()
            TopTabBar(initState = 1, onClearArchive = viewModel::clearArchive)

            Box(modifier = Modifier.fillMaxSize(1f).align(Alignment.CenterHorizontally)) {
                Column{
                    if(!devicePaired.value) {
                        viewModel.requestQRCode()
                        HostPairWidget( viewModel = viewModel, onFinishedPairing = ::onAcceptPairing)
                    } else {
                        Text("Pairing Successful. Now Sending Notes to Phone")
                        if(isConnected.value)
                            Text("Currently connected")
                    }
                }
            }
        }
    }
}
