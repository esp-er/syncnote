package com.patriker.syncnote.domain

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import com.patriker.syncnote.theme.ThemeSettingsShared
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

        //val primaryVariant = awtColor("0xFF7C7A7C") // light gray
        //val surface = awtColor("0xFF363436")
        val foreground = awtColor("0xFF333333")
        val background = awtColor("0xFFFFFFFF")
        val foregroundDark = awtColor("0xFF7C7A7C")
        val backgroundDark = awtColor("0xFF222022")

        val (pickFG, pickBG) = if(ThemeSettingsShared.isDarkThemeEnabledProp)
                                Pair(foregroundDark, backgroundDark)
                               else
                                Pair(foreground, background)




        val qrData = QRCode("$netAdrr/$newRandomId")
            //.render()
            .render(30,30, pickBG.rgb, pickFG.rgb, pickBG.rgb)

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