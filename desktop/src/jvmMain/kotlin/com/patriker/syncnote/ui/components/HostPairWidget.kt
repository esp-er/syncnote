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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
        Column(modifier = Modifier.align(Alignment.TopCenter)){ //TODO: generate new QR code when theme changes (dark/light)
            val foreground = MaterialTheme.colors.onSurface
           if(!pairingRequest) {

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
            else{
               Row {
                   Text("Now pairing with $pairDevice.value")
               }
           }
        }
    }

}
