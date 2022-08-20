package com.patriker.syncnote

import androidx.compose.ui.graphics.ImageBitmap
import com.patriker.syncnote.data.ExternFlowRepository
import com.patriker.syncnote.data.ExternRepository
import com.patriker.syncnote.data.FlowRepository
import com.patriker.syncnote.domain.NoteProperty
import com.patriker.syncnote.data.Repository
import com.patriker.syncnote.domain.QRGenerator
import com.patriker.syncnote.networking.PairingResult
import com.patriker.syncnote.networking.ServerControl
import com.patriker.syncnote.networking.SyncServer
import com.patriker.syncnote.routing.NotesRouter
import com.patriker.syncnote.routing.Screen
import kotlinx.coroutines.*
import kotlinx.datetime.Clock

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import com.russhwolf.settings.Settings
import org.apache.commons.lang3.ThreadUtils.sleep

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

        println("isPaired?: + ${appConfig.getBoolean("isPaired", false)} " )
        println("paired Device: + ${appConfig.getString("pairedDevice", "none")} " )
        println("prefs DIR: ${System.getProperty("java.util.prefs.userRoot")}")
        //println("PREFSDIR2: ${Preferences.userRoot().absolutePath()}")

        viewModelScope = getCorScope()
        startServer()
    }
    fun startServer(wasRestarted: Boolean = false){
        ServerControl.SyncOutdated.set(true)
        server = SyncServer(this, appConfig.getBoolean("isPaired", false), appConfig.getString("pairedDevice", "Unknown")).apply { //TODO: inject syncserver into constructor instead
            serverJob = CoroutineScope(Dispatchers.IO).launch {

                var configPort = appConfig.getInt("port")
                val port = if(wasRestarted) configPort + 1 else configPort
                    //testStart()
                start(listenPort = port)
            }
        }
        //_clientPairRequest = server.clientWishesToPair

        //Important scoping here!! TODO: Add this to android
        viewModelScope.launch{
            launch{
                server.clientWishesToPair.collect(){
                    _clientPairRequest.value = server.clientWishesToPair.value
                    _pairDeviceName.value = server.deviceName.value
                    yield()
                }
            }
            //_isPaired = server.isPairingDone
            launch {
                server.receivedNotes.collect {
                    if(it.isNotEmpty())
                        clearAndUpdateCache(it)
                    yield()
                }
            }
            launch{
                server.pairingResult.collect{
                    if(!appConfig.getBoolean("isPaired",false)) {
                        println("Saving isPaired:$it")
                        if(it.Paired != _isPaired.value) {
                            savePairingState(it)
                            _pairDeviceName.value = it.deviceName
                            _isPaired.value = it.Paired
                            println("INCOMING device name ${it.deviceName}")
                        }
                    }
                    yield()
                }
            }
        }
    }

    fun stopServer(){
        if(this::server.isInitialized){
            server.stop()
            serverJob.cancel()
        }
    }

    val desktopRepo = FlowRepository(repository)
    val cacheRepo = ExternFlowRepository(cacheRepository)

    val notes: StateFlow<List<NoteProperty>> by lazy{ desktopRepo.getMainNotes() }
    val notesInArchive: StateFlow<List<NoteProperty>> by lazy { desktopRepo.getArchivedNotes() }
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


    private var _clientPairRequest: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val clientPairRequest: StateFlow<Boolean> = _clientPairRequest

    private var _isPaired: MutableStateFlow<Boolean> = MutableStateFlow(appConfig.getBoolean("isPaired", false))
    val isPaired: StateFlow<Boolean> = _isPaired

    private var _isSyncing = MutableStateFlow(false)
    val isSyncing: StateFlow<Boolean>
        get(){
            return if(this::server.isInitialized) _isSyncing else MutableStateFlow(false)
        }
    private var _pairDeviceName = MutableStateFlow(appConfig.getString("pairedDevice", "None"))
    //var pairDeviceName  = _pairDeviceName
    val pairDeviceName: StateFlow<String> = _pairDeviceName

    fun setSyncingState(b: Boolean) {
        _isSyncing.let {
            it.value = b
        }
    }

    fun resetPairing(){
        stopServer()
        clearAndUpdateCache(listOf<NoteProperty>())
        _isPaired.value = false
        _pairDeviceName.value = "None"
        _isSyncing.value = false
        appConfig.putBoolean("isPaired", false)
        appConfig.putString("pairedDevice", "None")
        startServer(wasRestarted = true)
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
            desktopRepo.archiveNote(note.id)
            ServerControl.SyncOutdated.set(true)
        }
    }

    fun clearArchive(){
        viewModelScope.launch(Dispatchers.IO) {
            desktopRepo.deleteArchivedNotes()
        }
    }

    fun togglePin(note: NoteProperty) {
        viewModelScope.launch(Dispatchers.IO){
            if (note.isPinned) desktopRepo.unpinNote(note.id)
            else desktopRepo.pinNote(note.id)
            ServerControl.SyncOutdated.set(true)
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