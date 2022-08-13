package com.patriker.syncnote

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Surface
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.runtime.*
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.toAwtImage
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.loadImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.useResource
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.*
import com.patriker.syncnote.ui.screens.ArchiveScreen
//import com.patriker.syncnote.ui.screens.ArchiveScreen
import com.patriker.syncnote.ui.screens.NotesScreen
import com.patriker.syncnote.ui.screens.SaveNoteScreen
import com.raywenderlich.jetnotes.initKoin
import com.raywenderlich.jetnotes.MainViewModel
import com.raywenderlich.jetnotes.routing.NotesRouter
import com.raywenderlich.jetnotes.routing.Screen
import com.patriker.syncnote.ui.SyncNoteDesktopTheme
import com.patriker.syncnote.ui.getScaleFactor
import com.patriker.syncnote.ui.screens.SyncScreen
import java.awt.Component
import com.russhwolf.settings.Settings
import org.koin.core.Koin

import java.awt.Toolkit


import kotlinx.coroutines.*
import java.awt.Dimension
import javax.swing.text.StyleConstants.Size


//import com.raywenderlich.compose.theme.AppTheme
//import com.raywenderlich.compose.ui.MainView

data class WindowInfo(val windowName: String, val windowState: WindowState)


@ExperimentalMaterialApi
@OptIn(ExperimentalComposeUiApi::class)
fun main(): Unit {

    System.setProperty("sun.java2d.uiScale", "1.0")
    application {
        val scale = System.getProperty("sun.java2d.uiScale")
        println("java2d scale: $scale")


        //val dpi: Int = Toolkit.getDefaultToolkit().screenResolution //gets DPI
        val screenSize: Dimension = Toolkit.getDefaultToolkit().screenSize
        val (width, height) = listOf(screenSize.width, screenSize.height)


        val win_w = 600
        val win_h = 800
        val center_x = (width / 2 - win_w / 2).dp
        val center_y = (height / 2 - win_h / 2).dp


        val state = rememberWindowState(
            width = 600.dp, height = 500.dp,
            //position = WindowPosition(center_x,center_y)
            position = WindowPosition(1900.dp, 830.dp)
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
            //alwaysOnTop = pinWindow
        ) {
            val icon = painterResource("sync2_256.png")
            val density = LocalDensity.current
            SideEffect {
                window.iconImage = icon.toAwtImage(density, LayoutDirection.Ltr, Size(256f, 256f))
            }

            Surface {
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

}


