package com.raywenderlich.android.jetnotes

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.compose.setContent
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewModelScope
import com.raywenderlich.android.jetnotes.ui.screens.*
import com.raywenderlich.jetnotes.routing.NotesRouter
import com.raywenderlich.jetnotes.routing.Screen
import com.raywenderlich.jetnotes.theme.SyncNoteTheme
import com.raywenderlich.jetnotes.MainViewModel
import com.raywenderlich.jetnotes.data.SyncClient
import com.raywenderlich.jetnotes.initKoin
import kotlinx.coroutines.*
import org.koin.androidx.compose.getViewModel
import org.koin.androidx.viewmodel.ext.android.getViewModel


/**
 * Main activity for the app.
 */

@ExperimentalMaterialApi
class MainActivity : AppCompatActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    //var koin = initKoin().koin
    //val deviceModel = android.os.Build.MODEL
    //val product =  android.os.Build.PRODUCT

    setContent {
      SyncNoteTheme {
        MainActivityScreen()
      }
    }
  }
}

@ExperimentalMaterialApi
@Composable
private fun MainActivityScreen(viewModel: MainViewModel = getViewModel()) { //Koin injects ViewModel
  Surface {
    when (NotesRouter.currentScreen) {
      is Screen.Notes -> NotesScreen(viewModel)
      is Screen.NewNote -> SaveNoteScreen(viewModel, "New Note")
      is Screen.EditNote -> SaveNoteScreen(viewModel, "Edit Note")
      is Screen.Archive -> ArchiveScreen(viewModel) //ArchiveScreen(viewModel)
      is Screen.Synced -> SyncedNoteScreen(viewModel)
      is Screen.Pairing -> PairingScreen(viewModel)
    }
  }
}
