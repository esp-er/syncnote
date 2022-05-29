package com.raywenderlich.jetnotes

import androidx.compose.ui.geometry.Offset
import com.raywenderlich.jetnotes.data.ExternRepository
import com.raywenderlich.jetnotes.domain.NoteProperty
import com.raywenderlich.jetnotes.data.Repository
import com.raywenderlich.jetnotes.routing.NotesRouter
import com.raywenderlich.jetnotes.routing.Screen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock

//Contains the app state
actual class MainViewModel actual constructor(private val repository: Repository, private val cacheRepository: ExternRepository) : BaseViewModel() {

    val notes: List<NoteProperty> get() = repository.getMainNotes()
    val notesinArchive: List<NoteProperty> get() = repository.getArchivedNotes()

    val cachedNotes: List<NoteProperty> get() = cacheRepository.getNotes()

    private var _noteEntry = NoteProperty()
    val noteEntry: NoteProperty = _noteEntry

    fun onCreateNewNoteClick() {
        _noteEntry = NoteProperty() //Create a new note
        NotesRouter.navigateTo(Screen.SaveNote)
    }

    fun getRepoReference(): Repository{ //TODO: remove quick hack to share repo with SyncServer
        return repository
    }

    fun onNoteClick(note: NoteProperty) { //Pass in an existing note
        _noteEntry = note
        NotesRouter.navigateTo(Screen.SaveNote)
    }

    fun onNoteCheckedChange(note: NoteProperty) {
        /*viewModelScope.launch(Dispatchers.Default) {
            repository.insertNote(note)
        }*/
    }

    fun onNoteEntryChange(note: NoteProperty) {
        _noteEntry = note
    }

    fun saveNote(note: NoteProperty) {
        //withContext(Dispatchers.Main) {


        runBlocking{
            withContext(Dispatchers.IO) {
                repository.saveNote(note.copy(editDate = Clock.System.now()))
            }

            /* TODO: Will withContext block run asynchronously? */
            /* check and implement all functions that deal with repository in async*/
            //withContext(Dispatchers.Main) {
            NotesRouter.navigateTo(Screen.Notes)
            _noteEntry = NoteProperty()
            //}
        }
    }
    fun moveNoteToTrash(note: NoteProperty) {
        //viewModelScope.launch(Dispatchers.Default) { //TODO: learn about why viewModelscope is needed
            repository.archiveNote(note.id)
         //   withContext(Dispatchers.Main) {
            NotesRouter.navigateTo(Screen.Notes)
          //  }
        //}
    }

    fun restoreNoteFromArchive(note: NoteProperty){
        repository.restoreNote(note.id)
        NotesRouter.navigateTo(Screen.Notes)
    }

    fun permaDeleteNote(note: NoteProperty){
        repository.deleteNote(note.id)
            /*withContext(Dispatchers.Main) {
                when(NotesRouter.currentScreen) {
                    is Screen.SaveNote -> NotesRouter.navigateTo(Screen.Archive)
                    else -> NotesRouter.navigateTo(NotesRouter.currentScreen)
                }
            }*/
    }
}