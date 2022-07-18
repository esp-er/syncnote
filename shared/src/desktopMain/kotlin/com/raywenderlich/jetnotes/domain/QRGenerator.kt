package com.raywenderlich.jetnotes.domain

import androidx.compose.material.MaterialTheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import io.github.g0dkar.qrcode.QRCode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import io.github.g0dkar.qrcode.render.Colors


class QRGenerator{
    private val qrBitmap: MutableStateFlow<ImageBitmap?> by lazy{
        MutableStateFlow(null)
    }
    private val pairingStr: MutableStateFlow<String> by lazy{
        MutableStateFlow("")
    }

    fun getQR(): StateFlow<ImageBitmap?> = qrBitmap.asStateFlow()
    fun getPairingString(): StateFlow<String> = pairingStr.asStateFlow()

    suspend fun renderQRBitmap(): Unit = renderQRBitmap(ListNets.getFirstLocalIP())

    suspend fun renderQRBitmap(ipAddr: String, port: String = "9009"): Unit = withContext(Dispatchers.IO){
        val newRandomId = random8Id()
        HostKey.value = newRandomId
        val netAdrr = "$ipAddr:$port"

        val primaryVariant = awtColor("0xFF7C7A7C") // light grayI
        val surface = awtColor("0xFF363436")
        val foreground = awtColor("0xFF7C7A7C")
        val background = awtColor("0xFF222022") //TODO: get these colors from Theme instead (can't use Composable)


        val qrData = QRCode("$netAdrr/$newRandomId")
            //.render()
            .render(30,30, foreground.rgb, background.rgb, foreground.rgb)

        val imageBytes = ByteArrayOutputStream().also {
            qrData.writeImage(it, "PNG")
        }.toByteArray()

        val bitmap = org.jetbrains.skia.Image.makeFromEncoded(imageBytes).toComposeImageBitmap()

        pairingStr.value = "$netAdrr:$newRandomId"
        qrBitmap.value = bitmap
    }

    private fun awtColor(hex: String): java.awt.Color {
        val c = hex.drop(4)
        return java.awt.Color.decode("0x$c")
    }


}