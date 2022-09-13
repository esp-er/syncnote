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
        //appConfig.putBoolean("isPaired", false)

        //TODO: figure out when to append this keys (not here)
        appConfig.putString("sharedCode", "ALPHA")
        if(appConfig.getBoolean("isPaired", false)) {
            Log.d("SyncNote:", "Attempting connection to saved Host")
            this.attemptConnection(true)
        }
        else{
            Log.d("SyncNote:", "NOT PAIRED")
        }
    }
    private val androRepo = AndroidRepository(repository)
    private val androCache = AndroidExternRepository(cacheRepository)

    val host = HostData(appConfig.getString("hostAddress"),
        appConfig.getInt("port"),
        "/syncnote"
    ) //TODO: save this to settings

    private lateinit var syncClient: SyncClient
    fun attemptConnection(pairingState: Boolean = true) {
        val (attemptHost, attemptCode, name) =
                listOf(appConfig.getStringOrNull("hostAddress"),
                    appConfig.getStringOrNull("sharedCode"),
                    appConfig.getStringOrNull("deviceModel"))

        val attemptPort = appConfig.getIntOrNull("port")
        if (attemptPort == null || attemptHost == null || name == null || attemptCode == null) {
            Log.d(
                "SyncClient:",
                "host address or port missing, or pairing missing, aborting connection"
            )
            return
        }

        syncClient = SyncClient(
            this,
            host = HostData(attemptHost, attemptPort, "/syncnote"),
            pairingDone = pairingState,
            pairingData = PairingData(name, attemptCode)
        )
        /*MainScope().launch(Dispatchers.IO) {
        }*/
        MainScope().launch{
            syncClient.connect()
        }
        viewModelScope.launch(Dispatchers.Main) {
            //_isSyncing = sync.isSyncingLive
            //_isDevicePaired.value = true
            launch {
                syncClient.isSyncingLive.collect { _isSyncing.postValue(it) }
            }
            launch{
                syncClient.isPairingDone.collect { _isPaired.postValue(it); saveAppConfig(it)}
            }

        }
    }

    fun saveAppConfig(isPaired: Boolean){
        Log.d("SyncNote", "SAVING isPaired $isPaired")
       appConfig.putBoolean("isPaired", isPaired)
    }

    fun attemptPairConnection(host: HostData, sharedCode: String) {
        val model = appConfig.getString("deviceModel", "${android.os.Build.BRAND} ${android.os.Build.MODEL}")

        syncClient = SyncClient(
            this,
            host = HostData(host.address, host.port, "/syncnote"),
            pairingDone = false,
            pairingData = PairingData(model, sharedCode)
        )

        ClientControl.SendUpdates.set(true)
        MainScope().launch {
            syncClient.connect()
        }
        viewModelScope.launch(Dispatchers.Main) {
            launch {
                syncClient.isSyncingLive.collect {
                    _isSyncing.postValue(it)
                }
            }
            launch {
                syncClient.isPairingDone.collect {
                    _isPaired.postValue(it)
                    saveAppConfig(it)
                }
            }

        }
    }
    fun resetPairing(){
        if(this::syncClient.isInitialized)
            syncClient.abortConnection()
        clearAndUpdateCache(listOf<NoteProperty>())
        _isPaired.postValue(false)
        _isSyncing.postValue(false)
        appConfig.putBoolean("isPaired", false)
        appConfig.putString("pairedDevice", "None")
        ClientControl.SendUpdates.set(false)
    }


    //val  _isDevicePaired = MutableLiveData(appConfig.getBoolean("isPaired", false)) //TODO: retreive this from AndroidSettings provider instead
    private val _isPaired: MutableLiveData<Boolean> = MutableLiveData(appConfig.getBoolean("isPaired", false))
    val isPaired: LiveData<Boolean> = _isPaired

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
            ClientControl.SendUpdates.set(true)
            withContext(Dispatchers.Main) {
                NotesRouter.navigateTo(Screen.Notes)
                _noteEntry.value = NoteProperty()
            }
        }
    }
    fun archiveNote(note: NoteProperty) {
        viewModelScope.launch(Dispatchers.Default) {
            androRepo.archiveNote(note.id)
            ClientControl.SendUpdates.set(true)
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

            ClientControl.SendUpdates.set(true)
        }
    }

    fun restoreNoteFromArchive(note: NoteProperty){
        viewModelScope.launch(Dispatchers.Default) {
            androRepo.restoreNote(note.id)
            ClientControl.SendUpdates.set(true)
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
            ClientControl.SendUpdates.set(true)
            withContext(Dispatchers.Main) {
                /*when(NotesRouter.currentScreen) {
                    is Screen.SaveNote -> NotesRouter.navigateTo(Screen.Archive)
                    else -> NotesRouter.navigateTo(NotesRouter.currentScreen)
                }*/
            }
        }
    }
}

