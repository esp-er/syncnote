package com.raywenderlich.android.jetnotes

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.compose.setContent
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import com.raywenderlich.android.jetnotes.routing.JetNotesRouter
import com.raywenderlich.android.jetnotes.routing.Screen
import com.raywenderlich.android.jetnotes.theme.JetNotesTheme
import com.raywenderlich.android.jetnotes.ui.screens.NotesScreen
import com.raywenderlich.android.jetnotes.ui.screens.SaveNoteScreen
import com.raywenderlich.android.jetnotes.ui.screens.ArchiveScreen
import com.raywenderlich.android.jetnotes.viewmodel.MainViewModel
import com.raywenderlich.android.jetnotes.viewmodel.MainViewModelFactory

/**
 * Main activity for the app.
 */

@ExperimentalMaterialApi
class MainActivity : AppCompatActivity() {

  private val viewModel: MainViewModel by viewModels(factoryProducer = {
    MainViewModelFactory(
      this,
      (application as OpenNotesApp).dependencyInjector.repository
    )
  })

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {

      JetNotesTheme{
      //General idea: let people add arbitrary emoji / icon to note?
        //Search note/filter feature?
        MainActivityScreen(viewModel = viewModel)

      }

    }
  }
}

@ExperimentalMaterialApi
@Composable
private fun MainActivityScreen(viewModel: MainViewModel) {
  Surface {
    when (JetNotesRouter.currentScreen) {
      is Screen.Notes -> NotesScreen(viewModel)
      is Screen.SaveNote -> SaveNoteScreen(viewModel)
      is Screen.Archive -> ArchiveScreen(viewModel) //ArchiveScreen(viewModel)
    }
  }
}
