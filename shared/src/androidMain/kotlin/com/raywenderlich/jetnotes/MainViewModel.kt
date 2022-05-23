package com.raywenderlich.jetnotes

import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.ViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.raywenderlich.jetnotes.domain.NoteProperty
import com.raywenderlich.jetnotes.data.Repository
//import com.raywenderlich.jetnotes.routing.NotesRouter
//import com.raywenderlich.jetnotes.routing.Screen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock

import com.raywenderlich.jetnotes.data.AndroidRepository
import com.raywenderlich.jetnotes.routing.NotesRouter
import com.raywenderlich.jetnotes.routing.Screen

//Contains the app state
actual class MainViewModel actual constructor(private val repository: Repository) : BaseViewModel() {
    val androrepo = AndroidRepository(repository)
    val notes: LiveData<List<NoteProperty>> by lazy {
        androrepo.getMainNotes()
    }

    val notesInArchive: LiveData<List<NoteProperty>> by lazy {
        androrepo.getArchivedNotes()
    }

    private var _noteEntry = MutableLiveData(NoteProperty())
    val noteEntry: LiveData<NoteProperty> = _noteEntry


    val fabPos = MutableLiveData(Offset(0f,0f))
    fun setFabPos(newPos: Offset){
        fabPos.value = newPos
    }

    fun onCreateNewNoteClick() {
        _noteEntry.value = NoteProperty() //Create a new note
        NotesRouter.navigateTo(Screen.SaveNote)
    }

    fun onNoteClick(note: NoteProperty) { //Pass in an existing note
        _noteEntry.value = note
        NotesRouter.navigateTo(Screen.SaveNote)
    }

    fun onNoteCheckedChange(note: NoteProperty) {
        /*viewModelScope.launch(Dispatchers.Default) {
            repository.insertNote(note)
        }*/
    }

    fun onNoteEntryChange(note: NoteProperty) {
        _noteEntry.value = note
    }

    fun saveNote(note: NoteProperty) {
        viewModelScope.launch(Dispatchers.Default) {
            androrepo.saveNote(note.copy(editDate = Clock.System.now()))
            withContext(Dispatchers.Main) {
                NotesRouter.navigateTo(Screen.Notes)
                _noteEntry.value = NoteProperty()
            }
        }
    }
    fun moveNoteToTrash(note: NoteProperty) {
        viewModelScope.launch(Dispatchers.Default) {
            androrepo.archiveNote(note.id)
            withContext(Dispatchers.Main) {
                NotesRouter.navigateTo(Screen.Notes)
            }
        }
    }

    fun restoreNoteFromArchive(note: NoteProperty){
        viewModelScope.launch(Dispatchers.Default) {
            androrepo.restoreNote(note.id)
            withContext(Dispatchers.Main) {
                NotesRouter.navigateTo(Screen.Notes)
            }
        }
    }

    fun permaDeleteNote(note: NoteProperty){
        viewModelScope.launch(Dispatchers.Default) {
            androrepo.deleteNote(note.id)
            withContext(Dispatchers.Main) {
                /*when(NotesRouter.currentScreen) {
                    is Screen.SaveNote -> NotesRouter.navigateTo(Screen.Archive)
                    else -> NotesRouter.navigateTo(NotesRouter.currentScreen)
                }*/
            }
        }
    }


}