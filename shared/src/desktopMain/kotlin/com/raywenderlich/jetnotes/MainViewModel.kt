package com.raywenderlich.jetnotes

import androidx.compose.runtime.collectAsState
import androidx.compose.ui.graphics.ImageBitmap
import com.raywenderlich.jetnotes.data.ExternFlowRepository
import com.raywenderlich.jetnotes.data.ExternRepository
import com.raywenderlich.jetnotes.data.FlowRepository
import com.raywenderlich.jetnotes.domain.NoteProperty
import com.raywenderlich.jetnotes.data.Repository
import com.raywenderlich.jetnotes.domain.QRGenerator
import com.raywenderlich.jetnotes.networking.PairingResult
import com.raywenderlich.jetnotes.networking.ServerControl
import com.raywenderlich.jetnotes.networking.SyncServer
import com.raywenderlich.jetnotes.routing.NotesRouter
import com.raywenderlich.jetnotes.routing.Screen
import kotlinx.coroutines.*
import kotlinx.datetime.Clock

import java.util.prefs.Preferences

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.russhwolf.settings.Settings
import kotlinx.coroutines.flow.collect
import javax.swing.plaf.nimbus.State

//Contains the app state
actual class MainViewModel actual constructor(repository: Repository, private val cacheRepository: ExternRepository, private val appConfig: Settings, getCorScope: () -> CoroutineScope) : BaseViewModel() {
    val viewModelScope: CoroutineScope

    lateinit var server: SyncServer
    lateinit var serverJob: Job
    init{
        if(appConfig.getIntOrNull("port") == null)
            appConfig.putInt("port", 9000)
        if(appConfig.getBooleanOrNull("isPaired") == null)
            appConfig.putBoolean("isPaired", false)

        println("paired?: + ${appConfig.getBoolean("isPaired", false)} " )
        println("device?: + ${appConfig.getString("pairedDevice", "none")} " )
        println("PREFSDIR: ${System.getProperty("java.util.prefs.userRoot")}")
        println("PREFSDIR2: ${Preferences.userRoot().absolutePath()}")

        viewModelScope = getCorScope()
        startServer()
    }


    fun startServer(){
        server = SyncServer(this, appConfig.getBoolean("isPaired", false)).apply { //TODO: inject syncserver into constructor instead
            serverJob = viewModelScope.launch {
                withContext(Dispatchers.IO) {
                    println("starting ktor")
                    //testStart()
                    start(listenPort = appConfig.getInt("port"))
                }
            }
        }
        println("server started")
        //_clientPairRequest = server.clientWishesToPair

        //Important scoping here!! TODO: Add this to android
        viewModelScope.launch{
            _clientPairRequest = server.clientWishesToPair //Note the way we propagate stateflow is not clean...
            //_isPaired = server.isPairingDone
            _pairDeviceName = server.deviceName
            launch {
                server.receivedNotes.collect {
                    if(it.isNotEmpty())
                        clearAndUpdateCache(it)
                }
            }
            launch{
                server.pairingResult.collect{
                    if(!appConfig.getBoolean("isPaired",false)) {
                        println("saving $it")
                        savePairingState(it)
                        _isPaired.value = it.Paired
                    }
                }
            }
        }
    }

    fun stopServer(){
        if(this::server.isInitialized){
            serverJob.cancel()
            server.stop()
        }
    }

    val desktopRepo = FlowRepository(repository)
    val cacheRepo = ExternFlowRepository(cacheRepository)

    val notes: StateFlow<List<NoteProperty>> get() = desktopRepo.getMainNotes()
    val notesInArchive: StateFlow<List<NoteProperty>> get() = desktopRepo.getArchivedNotes()
    val qrgenerator = QRGenerator()

    val qrBitmapFlow: StateFlow<ImageBitmap?> = qrgenerator.getQR()
    val pairingInfoFlow: StateFlow<String> = qrgenerator.getPairingString()

    suspend fun savePairingState(dataToSave: PairingResult) = withContext(Dispatchers.IO){
        appConfig.putBoolean("isPaired", dataToSave.Paired)
        appConfig.putString("pairedDevice", dataToSave.deviceName)
    }

    val cachedNotes: StateFlow<List<NoteProperty>>
        get() = cacheRepo.getNotesFlow()

    val networkNotes: MutableStateFlow<List<NoteProperty>>
        get(){
            return if(this::server.isInitialized) server.receivedNotes else MutableStateFlow(emptyList())
        }

    //var isSyncing: MutableStateFlow<Boolean>


    private var _clientPairRequest: StateFlow<Boolean> = MutableStateFlow(false)
    val clientPairRequest: StateFlow<Boolean>
        get(){
            return if(this::server.isInitialized) _clientPairRequest else MutableStateFlow(false)
        }

    private var _isPaired: MutableStateFlow<Boolean> = MutableStateFlow(appConfig.getBoolean("isPaired", false))
    val isPaired: StateFlow<Boolean> = _isPaired

    private var _isSyncing = MutableStateFlow(false)
    val isSyncing: StateFlow<Boolean>
        get(){
            return if(this::server.isInitialized) _isSyncing else MutableStateFlow(false)
        }
    private var _pairDeviceName: StateFlow<String> = MutableStateFlow("Unknown")
    //var pairDeviceName  = _pairDeviceName
    val pairDeviceName: StateFlow<String>
        get(){
            return if(this::server.isInitialized) _pairDeviceName else MutableStateFlow("Unknown")
        }
    fun setSyncingState(b: Boolean) {
        _isSyncing.let {
            it.value = b
        }
    }


    private var _noteEntry = MutableStateFlow(NoteProperty())

    val noteEntry: StateFlow<NoteProperty> = _noteEntry

    fun hostAcceptedPairing(){
        ServerControl.PairAccept.set(true)
    }

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
    fun clearAndUpdateCache(externNotes: List<NoteProperty>){
        viewModelScope.launch(Dispatchers.IO) {
            cacheRepo.clearAndSaveAll(externNotes)
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