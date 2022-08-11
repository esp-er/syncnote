package com.patriker.syncnote.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.rememberDialogState
import com.raywenderlich.jetnotes.MainViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

//fun HostPairWidget(QRFlow: StateFlow<ImageBitmap?>, ipFlow: StateFlow<String>, pairRequestDevice: StateFlow<String>, onFinishedPairing: (String) -> Unit){
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HostPairWidget(viewModel: MainViewModel, onFinishedPairing: (String) -> Unit){

    val qrImage: State<ImageBitmap?> = viewModel.qrBitmapFlow.collectAsState()
    val ip: State<String> = viewModel.pairingInfoFlow.collectAsState()
    val pairDevice: State<String> = viewModel.pairDeviceName.collectAsState()
    val pairingIncoming = viewModel.clientPairRequest.collectAsState()
    val pairingRequest by derivedStateOf { pairDevice.value.isNotBlank() }
    var showDialog by remember { mutableStateOf(true) }

    val coroutineScope =  rememberCoroutineScope()

    Box(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colors.background),
        contentAlignment = Alignment.Center
    ){
        Column(modifier = Modifier.align(Alignment.TopCenter)){ //TODO: generate new QR code when theme changes (dark/light)
            val foreground = MaterialTheme.colors.onSurface
            //if(pairingIncoming.value && showDialog){
            if(pairingIncoming.value && showDialog){
                /*Dialog(
                    onCloseRequest = { showDialog = false; onFinishedPairing("asdf")},
                    state = rememberDialogState(position = WindowPosition(Alignment.Center))
                ) {
                    Text("Accept Pairing with new device?")
                }*/
                AlertDialog(
                    modifier = Modifier.fillMaxWidth(0.75f).fillMaxHeight(0.25f),
                    onDismissRequest = {
                        // Dismiss the dialog when the user clicks outside the dialog or on the back
                        // button. If you want to disable that functionality, simply use an empty
                        // onCloseRequest.
                        showDialog = false
                    },
                    title = {
                        Text(text = "Pairing Request")
                    },
                    text = {
                        Text("Would you like to pair with ${pairDevice.value}")
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                coroutineScope.launch {
                                    delay(100)
                                    showDialog = false
                                    onFinishedPairing("asdf")
                                }
                            }) {
                            Text("Confirm Pairing")
                        }
                    },
                    dismissButton = {
                        Button(
                            onClick = {
                                showDialog = false
                            }) {
                            Text("Cancel")
                        }
                    },
                    shape = RoundedCornerShape(8.dp)
                )


            }

            Text("Pair SyncNote with your phone to start syncing.",
                style = TextStyle(foreground),
                fontSize = 15.sp,
                modifier = Modifier.padding(top = 8.dp).align(Alignment.CenterHorizontally))

            Text("Scan the QR code below using the Android app!",
                style = TextStyle(foreground),
                fontSize = 15.sp,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .border(
                        4.dp,
                        MaterialTheme.colors.onBackground,
                        RoundedCornerShape(12.dp)
                    )
                    .fillMaxHeight(0.8f)
            ) {
                qrImage.value?.let { image ->
                    Image(image, "QR code")
                }
            }
            Row(modifier = Modifier.padding(top = 4.dp)) {
                Text("Manual connection info:", style = TextStyle(color = foreground))
            }
            Row {
                listOf("Address:", "Port:", "Pairing Code:").zip(ip.value.split(":"))
                    .forEach { (label, value) ->
                        Text(
                            "$label $value ",
                            style = TextStyle(color = foreground),
                            fontSize = 15.sp
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                    }
            }
        }
            /*else{
               Row {
                   Text("Now pairing with $pairDevice.value")
               }
           }*/
    }

}