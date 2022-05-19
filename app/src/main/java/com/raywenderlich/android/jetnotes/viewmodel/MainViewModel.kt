package com.raywenderlich.android.jetnotes.viewmodel

import android.util.Log
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.ViewModel
import com.raywenderlich.android.jetnotes.data.repository.Repository
import com.raywenderlich.android.jetnotes.domain.model.NoteModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.raywenderlich.android.jetnotes.data.repository.NotesRepository
import com.raywenderlich.android.jetnotes.domain.model.ColorModel
import com.raywenderlich.android.jetnotes.domain.model.NoteProperty
import com.raywenderlich.android.jetnotes.routing.JetNotesRouter
import com.raywenderlich.android.jetnotes.routing.Screen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.androidx.compose.getKoin


/**
 * View model used for storing the global app state.
 *
 * This view model is used for all screens.
 */


class MainViewModel(private val repository: NotesRepository) : ViewModel() {
    val notesNotInTrash: LiveData<List<NoteProperty>> by lazy {
        repository.getMainNotes()
    }

    val notesInTrash: LiveData<List<NoteProperty>> by lazy {
        repository.getArchivedNotes()
    }

    val colors: LiveData<List<ColorModel>> by lazy {
        repository.getAllColors()
    }

    private var _noteEntry = MutableLiveData(NoteProperty())
    val noteEntry: LiveData<NoteProperty> = _noteEntry


    val fabPos = MutableLiveData(Offset(0f,0f))
    fun setFabPos(newPos: Offset){
        fabPos.value = newPos
    }

    fun onCreateNewNoteClick() {
        _noteEntry.value = NoteProperty() //Create a new note
        JetNotesRouter.navigateTo(Screen.SaveNote)
    }

    fun onNoteClick(note: NoteProperty) { //Pass in an existing note
        _noteEntry.value = note
        JetNotesRouter.navigateTo(Screen.SaveNote)
    }

    fun onNoteCheckedChange(note: NoteProperty) {
        /*viewModelScope.launch(Dispatchers.Default) {
            repository.insertNote(note)
        }*/
    }

    fun onNoteEntryChange(note: NoteProperty) {
        Log.d("patrik","entry changed")
        _noteEntry.value = note
    }

    //TODO: Editing does not work
    fun saveNote(note: NoteProperty) {
        viewModelScope.launch(Dispatchers.Default) {
            Log.d("patrik", " Note saved")
            repository.saveNote(note)
            withContext(Dispatchers.Main) {
                JetNotesRouter.navigateTo(Screen.Notes)
                _noteEntry.value = NoteProperty()
            }
        }
    }
    fun moveNoteToTrash(note: NoteProperty) {
        viewModelScope.launch(Dispatchers.Default) {
            repository.archiveNote(note.id)
            withContext(Dispatchers.Main) {
                JetNotesRouter.navigateTo(Screen.Notes)
            }
        }
    }

    //TODO: fix the archive screen
    fun restoreNoteFromArchive(note: NoteProperty){
        viewModelScope.launch(Dispatchers.Default) {
            repository.restoreNote(note.id)
            withContext(Dispatchers.Main) {
                JetNotesRouter.navigateTo(Screen.Notes)
            }
        }
    }

    fun permaDeleteNote(note: NoteProperty){
        viewModelScope.launch(Dispatchers.Default) {
            repository.deleteNote(note.id)
            withContext(Dispatchers.Main) {
                when(JetNotesRouter.currentScreen) {
                    is Screen.SaveNote -> JetNotesRouter.navigateTo(Screen.Archive)
                    else -> JetNotesRouter.navigateTo(JetNotesRouter.currentScreen)
                }
            }
        }
    }


}

/*
class OldMainViewModel(private val notesRepository: NotesRepository, private val repository: Repository) : ViewModel() {

    private var _noteEntry = MutableLiveData(NoteModel())
    val noteEntry: LiveData<NoteModel> = _noteEntry


    val colors: LiveData<List<ColorModel>> by lazy {
        //TODO: replace with a static list of colors
        repository.getAllColors()
    }

    val fabPos = MutableLiveData(Offset(0f,0f))
    fun setFabPos(newPos: Offset){
        fabPos.value = newPos
    }

    fun onCreateNewNoteClick() {
        _noteEntry.value = NoteModel() //Create a new note
        JetNotesRouter.navigateTo(Screen.SaveNote)
    }
    fun onNoteClick(note: NoteModel) { //Pass in an existing note
        _noteEntry.value = note
        JetNotesRouter.navigateTo(Screen.SaveNote)
    }
    fun onNoteCheckedChange(note: NoteModel) {
        viewModelScope.launch(Dispatchers.Default) {
            repository.insertNote(note)
        }
    }

    fun onNoteEntryChange(note: NoteModel) {
        _noteEntry.value = note
    }
    fun saveNote(note: NoteModel) {
        viewModelScope.launch(Dispatchers.Default) {
            repository.insertNote(note)
            withContext(Dispatchers.Main) {
                JetNotesRouter.navigateTo(Screen.Notes)
                _noteEntry.value = NoteModel()
            }
        }
    }
    fun moveNoteToTrash(note: NoteModel) {
        viewModelScope.launch(Dispatchers.Default) {
            repository.moveNoteToArchive(note.id)
            withContext(Dispatchers.Main) {
                JetNotesRouter.navigateTo(Screen.Notes)
            }
        }
    }

    fun restoreNoteFromArchive(note: NoteModel){
        viewModelScope.launch(Dispatchers.Default) {
            repository.restoreNoteFromArchive(note.id)
            withContext(Dispatchers.Main) {
                JetNotesRouter.navigateTo(Screen.Notes)
            }
        }
    }

    fun permaDeleteNote(note: NoteModel){
        viewModelScope.launch(Dispatchers.Default) {
            repository.deleteNote(note.id)
            withContext(Dispatchers.Main) {
                when(JetNotesRouter.currentScreen) {
                    is Screen.SaveNote -> JetNotesRouter.navigateTo(Screen.Archive)
                    else -> JetNotesRouter.navigateTo(JetNotesRouter.currentScreen)
                }
            }
        }
    }

}
*/