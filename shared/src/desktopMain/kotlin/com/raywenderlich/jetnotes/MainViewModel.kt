package com.raywenderlich.jetnotes

import com.raywenderlich.jetnotes.data.ExternRepository
import com.raywenderlich.jetnotes.data.FlowRepository
import com.raywenderlich.jetnotes.domain.NoteProperty
import com.raywenderlich.jetnotes.data.Repository
import com.raywenderlich.jetnotes.routing.NotesRouter
import com.raywenderlich.jetnotes.routing.Screen
import kotlinx.coroutines.*
import kotlinx.datetime.Clock

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

//Contains the app state
actual class MainViewModel actual constructor(private val repository: Repository, private val cacheRepository: ExternRepository, getCorScope: () -> CoroutineScope) : BaseViewModel() {
    val viewModelScope: CoroutineScope
    init{
        viewModelScope = getCorScope()
    }

    val desktopRepo = FlowRepository(repository)
    val notes: StateFlow<List<NoteProperty>> get() = desktopRepo.getMainNotes()
    val notesInArchive: StateFlow<List<NoteProperty>> get() = desktopRepo.getArchivedNotes()

    val cachedNotes: List<NoteProperty> get() = cacheRepository.getNotes()

    var isSyncing = false
    var isPaired = false //TODO: add a "settings" repository for both android and desktop which  remembers these settings

    private var _noteEntry = MutableStateFlow(NoteProperty())

    val noteEntry: StateFlow<NoteProperty> = _noteEntry

    fun onCreateNewNoteClick() {
        _noteEntry.value = NoteProperty() //Create a new note
        NotesRouter.navigateTo(Screen.NewNote)
    }

    fun getRepoReference(): Repository{ //TODO: remove quick hack to share repo with SyncServer
        return repository
    }

    fun onNoteClick(note: NoteProperty) { //Pass in an existing note
        _noteEntry.value = note
        NotesRouter.navigateTo(Screen.EditNote)
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
        viewModelScope.launch(Dispatchers.IO) {
            desktopRepo.saveNote(note.copy(editDate = Clock.System.now()))
            withContext(Dispatchers.Default) {
                NotesRouter.navigateTo(Screen.Notes)
                _noteEntry.value = NoteProperty()
            }
        }
    }

    fun archiveNote(note: NoteProperty) {
        viewModelScope.launch(Dispatchers.IO) {
            desktopRepo.archiveNote(note.id)
        }
    }

    fun togglePin(note: NoteProperty) {
        viewModelScope.launch(Dispatchers.IO){
            if (note.isPinned) desktopRepo.unpinNote(note.id)
            else desktopRepo.pinNote(note.id)
        }
    }


    fun restoreNoteFromArchive(note: NoteProperty){
        viewModelScope.launch(Dispatchers.IO) {
            desktopRepo.restoreNote(note.id)
            withContext(Dispatchers.Default) { //TODO:perhaps not navigate on desktop?
                NotesRouter.navigateTo(Screen.Notes)
            }
        }
    }

    fun permaDeleteNote(note: NoteProperty){

        viewModelScope.launch(Dispatchers.IO) {
            desktopRepo.deleteNote(note.id)
            withContext(Dispatchers.Default) {
                when(NotesRouter.currentScreen) { //TODO: go back to archive if archived note
                    is Screen.NewNote -> NotesRouter.navigateTo(Screen.Notes)
                    is Screen.EditNote -> NotesRouter.navigateTo(Screen.Notes)
                    else -> NotesRouter.navigateTo(NotesRouter.currentScreen)
                }
            }
        }
            /*withContext(Dispatchers.Main) {
                when(NotesRouter.currentScreen) {
                    is Screen.SaveNote -> NotesRouter.navigateTo(Screen.Archive)
                    else -> NotesRouter.navigateTo(NotesRouter.currentScreen)
                }
            }*/
    }
}