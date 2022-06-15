package com.patriker.syncnote

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.ExperimentalMaterialApi
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
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyShortcut
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.*
import com.patriker.syncnote.networking.SyncServer
//import com.patriker.syncnote.ui.screens.ArchiveScreen
import com.patriker.syncnote.ui.screens.NotesScreen
import com.raywenderlich.jetnotes.initKoin
import com.raywenderlich.jetnotes.MainViewModel
import com.raywenderlich.jetnotes.routing.NotesRouter
import com.raywenderlich.jetnotes.routing.Screen
import com.raywenderlich.jetnotes.theme.SyncNoteTheme
import io.github.g0dkar.qrcode.QRCode
import org.koin.core.Koin


import kotlinx.coroutines.*
import java.io.ByteArrayOutputStream

import java.net.InetAddress



//import com.raywenderlich.compose.theme.AppTheme
//import com.raywenderlich.compose.ui.MainView

data class WindowInfo(val windowName: String, val windowState: WindowState)

lateinit var koin: Koin

lateinit var viewModel: MainViewModel


@ExperimentalMaterialApi
@OptIn(ExperimentalComposeUiApi::class)
fun main() = runBlocking {
        koin = initKoin().koin
    /*TODO: Re-enable all android *state* code that was commented out or removed while getting Desktop to compile*/

    /*
        val serverJob = launch(Dispatchers.IO){
            val server = SyncServer(viewModel.getRepoReference()).apply {
                start()
            }
        }


        val hostinfo = InetAddress.getLocalHost()
        val ipaddress = hostinfo.hostAddress!!
        val hostname = hostinfo.hostName!!
        val port: Int = 9000


        val qrData = QRCode("192.168.0.149:${port}").render()
        var imageBytes = ByteArrayOutputStream().also { qrData.writeImage(it, "PNG") }.toByteArray()
        val bitmap = org.jetbrains.skia.Image.makeFromEncoded(imageBytes).toComposeImageBitmap()
        //val tmp = qrData.writeImage()
        //val imageBytes = ByteArrayOutputStream().also { ImageIO.write(qrData, "PNG", it) }.toByteArray()

     */


        application {
            viewModel = MainViewModel(koin.get(), koin.get())
            val state =  rememberWindowState(width = 600.dp, height = 800.dp, position = WindowPosition(1800.dp, 800.dp))
            fun minimize() {
                state.isMinimized = true
            }

            Window(
                onCloseRequest = ::exitApplication,
                title = "SyncNote",
                state = state,
                transparent = false,
                undecorated = false,
                //alwaysOnTop = pinWindow
            ) {
                Surface {
                    SyncNoteTheme {
                        when (NotesRouter.currentScreen) {
                            is Screen.Notes -> NotesScreen(viewModel)
                            else -> NotesScreen(viewModel)
                            /*is Screen.SaveNote -> SaveNoteScreen(viewModel)
                            is Screen.Archive -> ArchiveScreen(viewModel) //ArchiveScreen(viewModel)
                            is Screen.Sync -> SyncScreen(viewModel)*/
                        }
                    }
                }
            }

        }

    }

/*
@Composable
fun DesktopMenuBar(){
    //Set up multiple windows
    var initialized by remember { mutableStateOf(false) }
    var windowCount by remember { mutableStateOf(1) }
    val windowList = remember { SnapshotStateList<WindowInfo>() }
    if (!initialized) {
        windowList.add(
            WindowInfo(
                "Timezone-${windowCount}",
                rememberWindowState()
            )
        )
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

        }
    }

}


@Composable
fun ShowQRCode(bitmap: ImageBitmap, ipaddress: String, port: String, hostname: String){
        Surface(modifier = Modifier.fillMaxSize()) {
            MaterialTheme() {
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
*/