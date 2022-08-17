package com.patriker.syncnote.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier

import androidx.compose.ui.Alignment
import com.patriker.syncnote.ui.components.*
import com.patriker.syncnote.MainViewModel


@ExperimentalMaterialApi
@Composable
fun SyncScreen(viewModel: MainViewModel, isHost: Boolean = false) {

    //TODO: implement signals to start listening when on this screen
    //Add popup for when client sends PairingData
    //Popup can only appear on this screen

    val isConnected = viewModel.isSyncing.collectAsState()
    val devicePaired = viewModel.isPaired.collectAsState()
    val syncingHost: String = "archlinux"

    var expandAllTrigger by remember { mutableStateOf(false) }
    fun onAcceptPairing(deviceName: String) {
        viewModel.hostAcceptedPairing()
    }


    //this delegate unwraps State<List<NoteModel>> into regular List<NoteModel>
    //val coroutineScope = rememberCoroutineScope()
    Box(modifier = Modifier.background(MaterialTheme.colors.background).fillMaxSize()) {
        Column {
            //TopBar(viewModel::onCreateNewNoteClick, { expandAllTrigger = !expandAllTrigger}, showExpandNotes = viewModel.isPaired.value)
            TopBar(viewModel::onCreateNewNoteClick, { expandAllTrigger = !expandAllTrigger}, false)  //DEBUG
            //horLineSeparator()
            TopTabBar(initState = 1, viewModel, onClearArchive = viewModel::clearArchive)
            if(!devicePaired.value) {
                Box(modifier = Modifier.fillMaxSize(1f).align(Alignment.CenterHorizontally)) {
                Column{
                        viewModel.requestQRCode()
                        HostPairWidget( viewModel = viewModel, onFinishedPairing = ::onAcceptPairing)
                    }
                }
            }
            else{
                NotesListImmutable(viewModel.cachedNotes, {}, {}, {}, expandAllTrigger = expandAllTrigger)
            }
        }
    }
}
