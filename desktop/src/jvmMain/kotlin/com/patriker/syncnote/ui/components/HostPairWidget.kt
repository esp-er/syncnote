package com.patriker.syncnote.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import com.raywenderlich.jetnotes.MainViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlin.coroutines.EmptyCoroutineContext

//fun HostPairWidget(QRFlow: StateFlow<ImageBitmap?>, ipFlow: StateFlow<String>, pairRequestDevice: StateFlow<String>, onFinishedPairing: (String) -> Unit){
@Composable
fun HostPairWidget(QRFlow: StateFlow<ImageBitmap?>, ipFlow: StateFlow<String>, onFinishedPairing: (String) -> Unit){

    val qrImage: State<ImageBitmap?> = QRFlow.collectAsState()
    val ip: State<String> = ipFlow.collectAsState()
    //val pairDevice: State<String> = pairRequestDevice.collectAsState()
    var pairDevice:MutableStateFlow<String> = MutableStateFlow("")
    val pairingRequest by derivedStateOf { pairDevice.value.isNotBlank() }

    Box(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colors.background),
        contentAlignment = Alignment.Center
    ){
        Column { //TODO: generate new QR code when theme changes (dark/light)
           if(!pairingRequest) {
               Row {
                   Text("Waiting for pairing request...")
               }
               Row(modifier = Modifier.padding(top = 8.dp)) {
                   Text("Manual connection info:")
               }
               Row {
                   listOf("Address:", "Port:", "Code:").zip(ip.value.split(":"))
                       .forEach { (label, value) ->
                           Text(
                               "$label $value ",
                               style = TextStyle(color = MaterialTheme.colors.onBackground)
                           )
                           Spacer(modifier = Modifier.width(16.dp))
                       }
               }

               Box(
                   modifier = Modifier.border(
                       4.dp,
                       MaterialTheme.colors.onSurface,
                       RoundedCornerShape(12.dp)
                   )
               ) {
                   qrImage.value?.let { image ->
                       Image(image, "QR code")
                   }
               }
           }
            else{
               Row {
                   Text("Now pairing with $pairDevice.value")
               }
           }
        }
    }

}
