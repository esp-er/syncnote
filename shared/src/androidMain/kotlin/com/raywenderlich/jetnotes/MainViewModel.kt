package com.raywenderlich.jetnotes

import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.raywenderlich.jetnotes.data.*
import com.raywenderlich.jetnotes.data.network.HostData
import com.raywenderlich.jetnotes.domain.NoteProperty
//import com.raywenderlich.jetnotes.routing.NotesRouter
//import com.raywenderlich.jetnotes.routing.Screen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock

import com.raywenderlich.jetnotes.routing.NotesRouter
import com.raywenderlich.jetnotes.routing.Screen
import kotlinx.coroutines.CoroutineScope

//Contains the app state
actual class MainViewModel actual constructor(private val repository: Repository, private val cacheRepository: ExternRepository, getCorScope: () -> CoroutineScope) : BaseViewModel() {
    val androrepo = AndroidRepository(repository)
    val androcache = AndroidExternRepository(cacheRepository)

/*
    init{
        viewModelScope.launch{
            val note = NoteProperty(
                id = "NEW",
                title = "TEST",
                content = "test",
                colorId = 0,
                false,
                false,
                false
            )
            androcache.saveNote(note)

        }
    }*/

    val host = HostData("192.168.0.149", 9000, "/syncnote") //TODO: save this to settings
                                                                                // and create a class/function
                                                                                //that can determine this
    val hasPairedHost = MutableLiveData(true) //TODO: retreive this from AndroidSettings provider instead
    val sync = SyncClient(androcache, host).apply {
        viewModelScope.launch(Dispatchers.IO) {
            connect()
        }
    }

    val isSyncing = sync.isSocketConnected()

    val cachedNotes: LiveData<List<NoteProperty>> by lazy {
        androcache.getNotesLiveData()
    }

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
        NotesRouter.navigateTo(Screen.NewNote)
    }

    fun onNoteClick(note: NoteProperty) { //Pass in an existing note
        _noteEntry.value = note
        NotesRouter.navigateTo(Screen.NewNote)
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
    fun archiveNote(note: NoteProperty) {
        viewModelScope.launch(Dispatchers.Default) {
            androrepo.archiveNote(note.id)
            withContext(Dispatchers.Main) {
                NotesRouter.navigateTo(Screen.Notes)
            }
        }
    }

    fun togglePin(note: NoteProperty) {
        viewModelScope.launch(Dispatchers.Default) {
            if(note.isPinned) androrepo.unpinNote(note.id)
            else androrepo.pinNote(note.id)
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

