package com.patriker.syncnote

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import androidx.compose.material.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyShortcut
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.*
import com.patriker.syncnote.networking.Control
import com.patriker.syncnote.networking.SocketSession
import com.patriker.syncnote.networking.SyncServer
import com.patriker.syncnote.networking.configureSocketServer
import com.raywenderlich.jetnotes.initKoin
import com.raywenderlich.jetnotes.MainViewModel
import com.raywenderlich.jetnotes.domain.NEW_UUID
import com.raywenderlich.jetnotes.domain.NoteProperty
import io.github.g0dkar.qrcode.QRCode
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.koin.core.Koin
import org.koin.dsl.module
import java.lang.Thread.sleep
import java.util.*
import kotlin.concurrent.thread


import io.github.g0dkar.qrcode.render.*
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO
import org.jetbrains.skia.Image

import java.net.InetAddress



//import com.raywenderlich.compose.theme.AppTheme
//import com.raywenderlich.compose.ui.MainView

data class WindowInfo(val windowName: String, val windowState: WindowState)

lateinit var koin: Koin

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    koin = initKoin().koin


    val m = MainViewModel(koin.get(), koin.get())
    val server = SyncServer(m.getRepoReference()).apply{
        start()
    }

    val hostinfo = InetAddress.getLocalHost()
    val ipaddress = hostinfo.hostAddress!!
    val hostname = hostinfo.hostName!!
    val port: Int = 9000


    val qrData = QRCode("192.168.0.149:${port}").render()
    var imageBytes = ByteArrayOutputStream().also{qrData.writeImage(it, "PNG")}.toByteArray()
    val bitmap = org.jetbrains.skia.Image.makeFromEncoded(imageBytes).toComposeImageBitmap()
    //val tmp = qrData.writeImage()
    //val imageBytes = ByteArrayOutputStream().also { ImageIO.write(qrData, "PNG", it) }.toByteArray()


/*
    repeat(10) {
        m.saveNote(
            NoteProperty(
                id = UUID.randomUUID().toString(),
                title = "Note No $it",
                content = "test",
                colorId = 0,
                false,
                false,
                false
            )
        )
    }*/



    application {
        //Set up multiple windows
        var initialized by remember { mutableStateOf(false) }
        var windowCount by remember { mutableStateOf(1) }
        val windowList = remember { SnapshotStateList<WindowInfo>() }
        if (!initialized) {
            windowList.add(WindowInfo("Timezone-${windowCount}",
                rememberWindowState()))
            initialized = true
        }

        windowList.forEachIndexed { i, window ->
            Window(

                onCloseRequest = {
                    windowList.removeAt(i)
                },

                state = windowList[i].windowState,
                title = windowList[i].windowName
            ) {

                Surface {

                    MenuBar {
                        Menu("File", mnemonic = 'F') {
                            val nextWindowState = rememberWindowState()
                            Item(
                                "New", onClick = {
                                    windowCount++
                                    windowList.add(
                                        WindowInfo(
                                            "Timezone-${windowCount}",
                                            nextWindowState
                                        )
                                    )
                                },
                                shortcut = KeyShortcut(Key.N, ctrl = true)
                            )
                            Item(
                                "Open", onClick = { },
                                shortcut = KeyShortcut(Key.O, ctrl = true)
                            )
                            Item(
                                "Close",
                                onClick = { windowList.removeAt(i) },
                                shortcut = KeyShortcut(Key.W, ctrl = true)
                            )
                            Item(
                                "Save", onClick = { },
                                shortcut = KeyShortcut(Key.S, ctrl = true)
                            )
                            Separator()
                            Item("Exit", onClick = { windowList.clear() },)
                        }
                        Menu("Edit", mnemonic = 'E') {
                            Item(
                                "Cut", onClick = { },
                                shortcut = KeyShortcut(Key.X, ctrl = true)
                            )
                            Item(
                                "Copy", onClick = { },
                                shortcut = KeyShortcut(Key.C, ctrl = true)
                            )
                            Item(
                                "Paste", onClick = { },
                                shortcut = KeyShortcut(Key.V, ctrl = true)
                            )
                        }
                    }
                }

                Surface(modifier = Modifier.fillMaxSize()) {
                    MaterialTheme () {
                        Column {
                            Row {
                                Text("$ipaddress:${port}")
                                Spacer(Modifier.width(4.dp))
                                Text(hostname)
                            }
                            Box(modifier = Modifier.size(320.dp, 320.dp)) {
                                Image(bitmap, "QR code")
                            }
                        }
                    }
                }
            }
        }
    }

}
