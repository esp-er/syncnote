package com.patriker.syncnote

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Surface
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.runtime.*
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
import com.patriker.syncnote.ui.screens.SyncScreen
import org.koin.core.Koin

import java.awt.Toolkit


import kotlinx.coroutines.*
import java.awt.Dimension


//import com.raywenderlich.compose.theme.AppTheme
//import com.raywenderlich.compose.ui.MainView

data class WindowInfo(val windowName: String, val windowState: WindowState)

lateinit var koin: Koin

lateinit var viewModel: MainViewModel


@ExperimentalMaterialApi
@OptIn(ExperimentalComposeUiApi::class)
fun main() = runBlocking {
        koin = initKoin().koin


    /*
        val serverJob = launch(Dispatchers.IO){
            val server = SyncServer(viewModel.getRepoReference()).apply {
                start()
            }
        }

     */


        application {

            val test: Int = Toolkit.getDefaultToolkit().screenResolution //gets DPI
            val screenSize: Dimension = Toolkit.getDefaultToolkit().screenSize
            val (width, height) = listOf(screenSize.width, screenSize.height)

            val x = (width / 2 - 300).dp
            val y = (height / 2 - 400).dp

            println(test)

            val scope = rememberCoroutineScope() //This Scope won't interfere with Compose UI coroutine
            viewModel = MainViewModel(koin.get(), koin.get(), { scope })
            val state = rememberWindowState(width = 600.dp, height = 800.dp,
                //position = WindowPosition(x,y)
                position = WindowPosition(1950.dp, 630.dp)
                )
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
                    SyncNoteDesktopTheme {
                        when (NotesRouter.currentScreen) {
                            is Screen.Notes -> NotesScreen(viewModel)
                            is Screen.NewNote -> SaveNoteScreen(viewModel, "New Note")
                            is Screen.EditNote -> SaveNoteScreen(viewModel, "Edit Note")
                            is Screen.Archive -> ArchiveScreen(viewModel) //ArchiveScreen(viewModel)
                            is Screen.Sync -> SyncScreen(viewModel)
                            else -> NotesScreen(viewModel)
                        }
                    }
                }
            }

        }

    }
