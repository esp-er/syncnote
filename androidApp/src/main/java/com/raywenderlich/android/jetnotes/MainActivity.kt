package com.raywenderlich.android.jetnotes

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.compose.setContent
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import com.raywenderlich.jetnotes.routing.NotesRouter
import com.raywenderlich.jetnotes.routing.Screen
import com.raywenderlich.jetnotes.theme.SyncNoteTheme
import com.raywenderlich.android.jetnotes.ui.screens.NotesScreen
import com.raywenderlich.android.jetnotes.ui.screens.SaveNoteScreen
import com.raywenderlich.android.jetnotes.ui.screens.ArchiveScreen
import com.raywenderlich.android.jetnotes.ui.screens.SyncScreen
import com.raywenderlich.jetnotes.MainViewModel
import org.koin.androidx.compose.getViewModel


/**
 * Main activity for the app.
 */

@ExperimentalMaterialApi
class MainActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      SyncNoteTheme{
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
      is Screen.Sync -> SyncScreen(viewModel)
    }
  }
}
