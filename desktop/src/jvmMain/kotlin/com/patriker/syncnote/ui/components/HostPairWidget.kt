package com.patriker.syncnote.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.unit.dp
import com.raywenderlich.jetnotes.MainViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlin.coroutines.EmptyCoroutineContext

@Composable
fun HostPairWidget(QRFlow: StateFlow<ImageBitmap?>, ipFlow: StateFlow<String>){

    val qrImage: State<ImageBitmap?> = QRFlow.collectAsState()
    val ip: State<String> = ipFlow.collectAsState()

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ){
        Column {
            Text(ip.value)
            qrImage.value?.let { image ->
                Image(image, "QR code")
            }
        }
    }

}
