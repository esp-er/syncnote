package com.patriker.syncnote.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import com.raywenderlich.jetnotes.MainViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlin.coroutines.EmptyCoroutineContext

@Composable
fun HostPairWidget(QRFlow: StateFlow<ImageBitmap?>, ipFlow: StateFlow<String>){

    val qrImage: State<ImageBitmap?> = QRFlow.collectAsState()
    val ip: State<String> = ipFlow.collectAsState()

    Box(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colors.background),
        contentAlignment = Alignment.Center
    ){
        Column { //TODO: generate new QR code when theme changes (dark/light)
            Text(ip.value, style = TextStyle(color = MaterialTheme.colors.onBackground))
            Box(modifier = Modifier.border(4.dp, MaterialTheme.colors.onSurface, RoundedCornerShape(12.dp))) {
                qrImage.value?.let { image ->
                    Image(image, "QR code")
                }
            }
        }
    }

}
