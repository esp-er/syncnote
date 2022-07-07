package com.raywenderlich.jetnotes.domain

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import io.github.g0dkar.qrcode.QRCode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream


class QRGenerator{
    private val qrBitmap: MutableStateFlow<ImageBitmap?> by lazy{
        MutableStateFlow(null)
    }
    private val ip: MutableStateFlow<String> by lazy{
        MutableStateFlow("")
    }

    fun getQR(): StateFlow<ImageBitmap?> = qrBitmap.asStateFlow()
    fun getNetAddr(): StateFlow<String> = ip.asStateFlow()

    suspend fun renderQRBitmap(): Unit = renderQRBitmap(ListNets.getFirstLocalIP())

    suspend fun renderQRBitmap(ipAddr: String, port: String = "9009"): Unit = withContext(Dispatchers.IO){
        val newUUID = UUID().toString()
        HostKey.value = UUIDStr(newUUID)
        val netAdrr = "$ipAddr:$port"
        val qrData = QRCode("$netAdrr/$newUUID").render()

        val imageBytes = ByteArrayOutputStream().also {
            qrData.writeImage(it, "PNG")
        }.toByteArray()

        val bitmap = org.jetbrains.skia.Image.makeFromEncoded(imageBytes).toComposeImageBitmap()

        ip.value = netAdrr
        qrBitmap.value = bitmap
    }


}