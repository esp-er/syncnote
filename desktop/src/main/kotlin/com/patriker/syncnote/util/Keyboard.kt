package com.patriker.syncnote.util

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.input.key.*
import com.patriker.syncnote.MainViewModel
import com.patriker.syncnote.routing.NotesRouter
import com.patriker.syncnote.routing.Screen

@OptIn(ExperimentalComposeUiApi::class)
fun captureKeyboardShortcut(event: KeyEvent, viewModel: MainViewModel) : Boolean {
    if (NotesRouter.currentScreen == Screen.Notes) {
        when {
            (event.isCtrlPressed && event.key == Key.N && event.type == KeyEventType.KeyUp) -> {
                viewModel.onCreateNewNoteClick()
                return true
            }
            else ->  return false
        }
    }
    else if(NotesRouter.currentScreen == Screen.NewNote || NotesRouter.currentScreen == Screen.EditNote){
        when {
            (event.isCtrlPressed && event.key == Key.S && event.type == KeyEventType.KeyUp) -> {
                viewModel.saveNote(viewModel.noteEntry.value)
                return true
            }
            else ->  return false
        }
    }
    else {
        return false
    }
}