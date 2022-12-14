package com.patriker.syncnote.routing

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

sealed class Screen {
  object Notes : Screen()
  object NewNote : Screen()
  object EditNote : Screen()
  object Archive : Screen()
  object Synced : Screen()
  object Pairing : Screen()
 // object Chat: Screen()
}

//changes screen in MainActivity
object NotesRouter {
  var currentScreen: Screen by mutableStateOf(Screen.Notes)

  fun navigateTo(destination: Screen) {
    currentScreen = destination
  }
}
