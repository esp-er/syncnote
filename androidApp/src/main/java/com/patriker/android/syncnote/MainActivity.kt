package com.patriker.android.syncnote

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.compose.setContent
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import com.patriker.android.syncnote.ui.screens.*
import com.patriker.syncnote.routing.NotesRouter
import com.patriker.syncnote.routing.Screen
import com.patriker.android.syncnote.theme.SyncNoteTheme
import com.patriker.syncnote.MainViewModel
import org.koin.androidx.compose.getViewModel


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
