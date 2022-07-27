package com.raywenderlich.jetnotes

import android.util.Log
import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.raywenderlich.jetnotes.data.*
import com.raywenderlich.jetnotes.data.network.HostData
import com.raywenderlich.jetnotes.domain.NoteProperty
import com.raywenderlich.jetnotes.domain.PairingData
//import com.raywenderlich.jetnotes.routing.NotesRouter
//import com.raywenderlich.jetnotes.routing.Screen
import kotlinx.datetime.Clock

import com.raywenderlich.jetnotes.routing.NotesRouter
import com.raywenderlich.jetnotes.routing.Screen
import com.russhwolf.settings.Settings
import com.russhwolf.settings.get
import kotlinx.coroutines.*

//Contains the app state
actual class MainViewModel actual constructor(private val repository: Repository, private val cacheRepository: ExternRepository, private val appConfig: Settings, getCorScope: () -> CoroutineScope) : BaseViewModel() {
    init{
        if (appConfig.getStringOrNull("deviceModel") == null ) 
            appConfig.putString("deviceModel", android.os.Build.MODEL)
        val hostIp = appConfig.getStringOrNull("hostAddress")
        //if (hostIp == null){
           appConfig.putString("hostAddress", "10.0.2.2")
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
        sync.apply {
            viewModelScope.launch(Dispatchers.IO) {
                //test()
                connect()
            }

        }
        isSyncing = sync.isSyncingLive
        isDevicePaired = sync.isPairingDone
    }
    fun attemptPairConnection(host: HostData, sharedCode: String) {

        val model = appConfig.getString("deviceModel", android.os.Build.MODEL)
        sync = SyncClient(
            this,
            host = host,
            pairingDone = false,
            pairingData = PairingData(model, sharedCode)
        )
        sync.apply {
            viewModelScope.launch(Dispatchers.IO) {
                //test()
                connect()
            }

        }

        isSyncing = sync.isSyncingLive
        isDevicePaired = sync.isPairingDone
    }

    fun testConnect(host: HostData, sharedCode: String) {
        attemptConnection(false)
    }


    //val  _isDevicePaired = MutableLiveData(appConfig.getBoolean("isPaired", false)) //TODO: retreive this from AndroidSettings provider instead
    var isDevicePaired: LiveData<Boolean> = MutableLiveData(false)
    /*
    suspend fun setDevicePaired(b: Boolean) =
        viewModelScope.launch {
            _isDevicePaired.let {
                it.setValue(b)
                it.postValue(b)
            }
            Log.d("ANDROID MAINVIEWCONTROL", "${_isDevicePaired.value}")
        }
     */


    //val isSyncing = sync.isSocketConnected()
    //val _isSyncing = MutableLiveData(false)
    var isSyncing: LiveData<Boolean> = MutableLiveData(false)
    /*fun setSyncingState(b: Boolean) =
        viewModelScope.launch {
            _isSyncing.let {
                it.setValue(b)
                it.postValue(b)
            }
        }
     */

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

