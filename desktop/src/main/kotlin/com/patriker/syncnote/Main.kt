package com.patriker.syncnote

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.toAwtImage
import androidx.compose.ui.input.key.*
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.*
import com.patriker.syncnote.ui.screens.ArchiveScreen
//import com.patriker.syncnote.ui.screens.ArchiveScreen
import com.patriker.syncnote.ui.screens.NotesScreen
import com.patriker.syncnote.ui.screens.SaveNoteScreen
import com.patriker.syncnote.routing.NotesRouter
import com.patriker.syncnote.routing.Screen
import com.patriker.syncnote.ui.SyncNoteDesktopTheme
import com.patriker.syncnote.ui.screens.SyncScreen
import com.patriker.syncnote.util.captureKeyboardShortcut

import java.awt.Toolkit


import kotlinx.coroutines.*
import java.awt.Dimension


data class WindowInfo(val windowName: String, val windowState: WindowState)



@OptIn(ExperimentalComposeUiApi::class)
@ExperimentalMaterialApi
fun main(args: Array<String>) =
    application {
        val scale = System.getProperty("sun.java2d.uiScale")
        val vsync = System.getProperty("skiko.vsync.enabled")
        println("java2d scale: $scale")
        println("vsync: $vsync")


        //val dpi: Int = Toolkit.getDefaultToolkit().screenResolution //gets DPI
        val screenSize: Dimension = Toolkit.getDefaultToolkit().screenSize
        val (width, height) = listOf(screenSize.width, screenSize.height)


        val win_w = 500
        val win_h = 600
        val center_x = (width / 2 - win_w / 2).dp
        val center_y = (height / 2 - win_h / 2).dp


        val state = rememberWindowState(
            width = win_w.dp, height = win_h.dp,
            position = WindowPosition(center_x,center_y)
            //position = WindowPosition(1900.dp, 830.dp)
        )

        fun minimize() {
            state.isMinimized = true
        }

        val scope = rememberCoroutineScope() //This Scope won't interfere with Compose UI coroutine
        fun getCorScope(): CoroutineScope {
            return scope
        }

        val koin = initKoin().koin
        val viewModel = MainViewModel(koin.get(), koin.get(), koin.get(), ::getCorScope)

        Window(
            onCloseRequest = { viewModel.stopServer(); exitApplication() },
            title = "SyncNote",
            state = state,
            transparent = false,
            undecorated = false,
            onKeyEvent = {
                captureKeyboardShortcut(it, viewModel)
            }
            //alwaysOnTop = pinWindow
        ) {
            val icon = painterResource("syncicon_128.png")
            val density = LocalDensity.current
            SideEffect {
                window.iconImage = icon.toAwtImage(density, LayoutDirection.Ltr, Size(256f, 256f))
            }

            Surface{
                SyncNoteDesktopTheme {
                    when (NotesRouter.currentScreen) {
                        is Screen.Notes -> NotesScreen(viewModel)
                        is Screen.NewNote -> SaveNoteScreen(viewModel, "New Note")
                        is Screen.EditNote -> SaveNoteScreen(viewModel, "Edit Note")
                        is Screen.Archive -> ArchiveScreen(viewModel) //ArchiveScreen(viewModel)
                        is Screen.Synced -> SyncScreen(viewModel)
                        else -> NotesScreen(viewModel)
                    }
                }
            }
        }
    }
