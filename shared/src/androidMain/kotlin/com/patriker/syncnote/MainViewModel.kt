package com.patriker.syncnote

import android.util.Log
import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.patriker.syncnote.data.*
import com.patriker.syncnote.data.network.HostData
import com.patriker.syncnote.domain.NoteProperty
import com.patriker.syncnote.domain.PairingData
//import com.raywenderlich.jetnotes.routing.NotesRouter
//import com.raywenderlich.jetnotes.routing.Screen
import kotlinx.datetime.Clock

import com.patriker.syncnote.routing.NotesRouter
import com.patriker.syncnote.routing.Screen
import com.russhwolf.settings.Settings
import kotlinx.coroutines.*

//Contains the app state
actual class MainViewModel actual constructor(private val repository: Repository, private val cacheRepository: ExternRepository, private val appConfig: Settings, getCorScope: () -> CoroutineScope) : BaseViewModel() {
    init{
        appConfig.putString("deviceModel", "${android.os.Build.BRAND} ${android.os.Build.MODEL}")
        val hostIp = appConfig.getStringOrNull("hostAddress")
        //if (hostIp == null){
           appConfig.putString("hostAddress", "192.168.0.149")
        //}
        appConfig.putInt("port", 9000)
        appConfig.putBoolean("isPaired", false)

        //TODO: figure out when to append this keys (not here)
        appConfig.putString("sharedCode", "ASDFQWER")
    }
    private val androRepo = AndroidRepository(repository)
    private val androCache = AndroidExternRepository(cacheRepository)

    val host = HostData(appConfig.getString("hostAddress"),
        appConfig.getInt("port"),
        "/syncnote"
    ) //TODO: save this to settings

    private lateinit var sync: SyncClient
    fun attemptConnection(testPair: Boolean = true) {
        val (attemptHost, attemptCode, name) = listOf(
            appConfig.getStringOrNull("hostAddress"),
            appConfig.getStringOrNull("sharedCode"),
            appConfig.getStringOrNull("deviceModel")

        )
        val attemptPort = appConfig.getIntOrNull("port")
        if (attemptPort == null || attemptHost == null || name == null || attemptCode == null) {
            Log.d(
                "SyncClient:",
                "host address or port missing, or pairing missing, aborting connection"
            )
            return
        }

        sync = SyncClient(
            this,
            host = HostData(attemptHost, attemptPort, "/syncnote"),
            pairingDone = testPair,
            pairingData = PairingData(name, attemptCode)
        )
        /*MainScope().launch(Dispatchers.IO) {
        }*/
        MainScope().launch{
            sync.connect()
        }
        viewModelScope.launch(Dispatchers.Main) {
            //_isSyncing = sync.isSyncingLive
            //_isDevicePaired.value = true
            launch {
                sync.isSyncingLive.collect { _isSyncing.postValue(it) }
            }
            launch{
                sync.isPairingDone.collect { _isDevicePaired.postValue(it) }
            }

        }
    }
fun attemptPairConnection(host: HostData, sharedCode: String) {
    val model = appConfig.getString("deviceModel", "${android.os.Build.BRAND} ${android.os.Build.MODEL}")
            sync = SyncClient(
        this,
        host = HostData(host.address, host.port, "/syncnote"),
        pairingDone = false,
        pairingData = PairingData(model, sharedCode)
    )
    MainScope().launch {
        sync.connect()
    }
    viewModelScope.launch(Dispatchers.Main) {
        launch {
            sync.isSyncingLive.collect {
                _isSyncing.postValue(it)
            }
        }
        launch {
            sync.isPairingDone.collect {
                _isDevicePaired.postValue(it)
            }
        }

    }
}

    fun testConnect(host: HostData, sharedCode: String = "ASDFQWER") {
        attemptConnection(false)
    }

    //val  _isDevicePaired = MutableLiveData(appConfig.getBoolean("isPaired", false)) //TODO: retreive this from AndroidSettings provider instead
    private val _isDevicePaired: MutableLiveData<Boolean> = MutableLiveData(false)
    var isDevicePaired: LiveData<Boolean> = _isDevicePaired

    private val _isSyncing: MutableLiveData<Boolean> = MutableLiveData(false)
    var isSyncing: LiveData<Boolean> = _isSyncing

    val cachedNotes: LiveData<List<NoteProperty>> by lazy {
        androCache.getNotesLiveData()
    }
    val notes: LiveData<List<NoteProperty>> by lazy {
        androRepo.getMainNotes()
    }
    val notesInArchive: LiveData<List<NoteProperty>> by lazy {
        androRepo.getArchivedNotes()
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
            androRepo.saveNote(note.copy(editDate = Clock.System.now()))
            withContext(Dispatchers.Main) {
                NotesRouter.navigateTo(Screen.Notes)
                _noteEntry.value = NoteProperty()
            }
        }
    }
    fun archiveNote(note: NoteProperty) {
        viewModelScope.launch(Dispatchers.Default) {
            androRepo.archiveNote(note.id)
            withContext(Dispatchers.Main) {
                NotesRouter.navigateTo(Screen.Notes)
            }
        }
    }

    fun clearArchive(){
       // viewModelScope
    }

    fun togglePin(note: NoteProperty) {
        viewModelScope.launch(Dispatchers.Default) {
            if(note.isPinned) androRepo.unpinNote(note.id)
            else androRepo.pinNote(note.id)
        }
    }

    fun restoreNoteFromArchive(note: NoteProperty){
        viewModelScope.launch(Dispatchers.Default) {
            androRepo.restoreNote(note.id)
            withContext(Dispatchers.Main) {
                NotesRouter.navigateTo(Screen.Notes)
            }
        }
    }

    fun clearAndUpdateCache(externNotes: List<NoteProperty>){
        viewModelScope.launch(Dispatchers.Default) {
            androCache.clearAndSaveAll(externNotes)
        }
    }

    fun permaDeleteNote(note: NoteProperty){
        viewModelScope.launch(Dispatchers.Default) {
            androRepo.deleteNote(note.id)
            withContext(Dispatchers.Main) {
                /*when(NotesRouter.currentScreen) {
                    is Screen.SaveNote -> NotesRouter.navigateTo(Screen.Archive)
                    else -> NotesRouter.navigateTo(NotesRouter.currentScreen)
                }*/
            }
        }
    }
}

