package com.raywenderlich.jetnotes

import androidx.compose.ui.graphics.ImageBitmap
import com.raywenderlich.jetnotes.data.ExternRepository
import com.raywenderlich.jetnotes.data.FlowRepository
import com.raywenderlich.jetnotes.domain.NoteProperty
import com.raywenderlich.jetnotes.data.Repository
import com.raywenderlich.jetnotes.domain.QRGenerator
import com.raywenderlich.jetnotes.networking.ServerControl
import com.raywenderlich.jetnotes.networking.SyncServer
import com.raywenderlich.jetnotes.routing.NotesRouter
import com.raywenderlich.jetnotes.routing.Screen
import kotlinx.coroutines.*
import kotlinx.datetime.Clock

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.russhwolf.settings.Settings

lateinit var server: SyncServer
//Contains the app state
actual class MainViewModel actual constructor(repository: Repository, private val cacheRepository: ExternRepository, private val appConfig: Settings, getCorScope: () -> CoroutineScope) : BaseViewModel() {
    val viewModelScope: CoroutineScope
    init{
        viewModelScope = getCorScope()
        startServer()
    }

    fun startServer(){
        server = SyncServer(this).apply { //TODO: inject syncserver into constructor instead
            viewModelScope.launch(Dispatchers.IO) {
                println("starting ktor")
                //testStart()
                start()
            }
        }
    }

    val desktopRepo = FlowRepository(repository)
    val notes: StateFlow<List<NoteProperty>> get() = desktopRepo.getMainNotes()
    val notesInArchive: StateFlow<List<NoteProperty>> get() = desktopRepo.getArchivedNotes()
    val qrgenerator = QRGenerator()

    val qrBitmapFlow: StateFlow<ImageBitmap?> = qrgenerator.getQR()
    val pairingInfoFlow: StateFlow<String> = qrgenerator.getPairingString()


    val cachedNotes: List<NoteProperty> get() = cacheRepository.getNotes()

    //var isSyncing: MutableStateFlow<Boolean>

    private var _isPaired = MutableStateFlow(false)
    //TODO: add a "settings" repository for both android and desktop which  remembers these settings
    val isPaired  = _isPaired.asStateFlow()
    fun setPairedState(b: Boolean) =
        _isPaired.let{
            it.value = b
        }

    private var _isSyncing = MutableStateFlow(false)
    val isSyncing  = _isSyncing.asStateFlow()
    fun setSyncingState(b: Boolean) =
        _isSyncing.let{
            it.value = b
        }


    private var _noteEntry = MutableStateFlow(NoteProperty())

    val noteEntry: StateFlow<NoteProperty> = _noteEntry

    fun onCreateNewNoteClick() {
        _noteEntry.value = NoteProperty() //Create a new note
        NotesRouter.navigateTo(Screen.NewNote)
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
                ServerControl.SyncOutdated.set(true)
                NotesRouter.navigateTo(Screen.Notes)
                _noteEntry.value = NoteProperty()
            }
        }
    }

    fun archiveNote(note: NoteProperty) {
        viewModelScope.launch(Dispatchers.IO) {
            ServerControl.SyncOutdated.set(true)
            desktopRepo.archiveNote(note.id)
        }
    }

    fun clearArchive(){
        viewModelScope.launch(Dispatchers.IO) {
            desktopRepo.deleteArchivedNotes()
        }
    }

    fun togglePin(note: NoteProperty) {
        viewModelScope.launch(Dispatchers.IO){
            ServerControl.SyncOutdated.set(true)
            if (note.isPinned) desktopRepo.unpinNote(note.id)
            else desktopRepo.pinNote(note.id)
        }
    }


    fun restoreNoteFromArchive(note: NoteProperty){
        viewModelScope.launch(Dispatchers.IO) {
            desktopRepo.restoreNote(note.id)
            ServerControl.SyncOutdated.set(true)
            withContext(Dispatchers.Default) { //TODO:perhaps not navigate on desktop?
                NotesRouter.navigateTo(Screen.Notes)
            }
        }
    }

    fun requestQRCode() {
        if(!isPaired.value) {
            viewModelScope.launch {
                qrgenerator.renderQRBitmap()
            }
        }
    }

    fun permaDeleteNote(note: NoteProperty){

        viewModelScope.launch(Dispatchers.IO) {
            desktopRepo.deleteNote(note.id)
            ServerControl.SyncOutdated.set(true)
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