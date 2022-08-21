package com.patriker.syncnote.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.patriker.syncnote.MainViewModel
import kotlinx.coroutines.*

//fun HostPairWidget(QRFlow: StateFlow<ImageBitmap?>, ipFlow: StateFlow<String>, pairRequestDevice: StateFlow<String>, onFinishedPairing: (String) -> Unit){
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HostPairWidget(viewModel: MainViewModel, onFinishedPairing: (String) -> Unit){

    val qrImage: State<ImageBitmap?> = viewModel.qrBitmapFlow.collectAsState()
    val ip: State<String> = viewModel.pairingInfoFlow.collectAsState()
    val ipAddr: State<String> = viewModel.qrIpFlow.collectAsState()
    val serverPort: State<String> = viewModel.qrPortFlow.collectAsState()
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
                    .align(alignment = Alignment.CenterHorizontally)
                    .fillMaxHeight(0.8f),
                contentAlignment = Alignment.Center
            ) {
                qrImage.value?.let { image ->
                    Image(image, "QR code")
                }
            }
            Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                Row(modifier = Modifier.padding(top = 4.dp)) {
                    Text("Manual connection info:", style = TextStyle(color = foreground))
                }
                Row {
                    SelectionContainer {
                        Text(
                            "Address: ${ipAddr.value}",
                            style = TextStyle(color = foreground),
                            fontSize = 15.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    SelectionContainer {
                        Text(
                            "Port: ${serverPort.value}",
                            style = TextStyle(color = foreground),
                            fontSize = 15.sp
                        )
                    }
                }

                Row {
                    ip.value.split(":").last().let { code ->
                        SelectionContainer {
                            Text(
                                text = "Code:",
                                style = TextStyle(color = foreground),
                                fontSize = 15.sp,
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        SelectionContainer {
                            Text(
                                text = code,
                                style = TextStyle(color = foreground),
                                fontSize = 16.sp,
                                letterSpacing = 2.sp
                            )
                        }
                    }
                }
            }
        }

    }
}

