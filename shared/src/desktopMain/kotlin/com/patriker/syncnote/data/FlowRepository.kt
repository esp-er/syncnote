package com.patriker.syncnote.data

import com.patriker.syncnote.data.Repository
import com.patriker.syncnote.domain.NoteProperty

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/*
import com.raywenderlich.android.jetnotes.data.database.dbmapper.DbMapperImpl
import com.raywenderlich.android.jetnotes.data.database.model.ColorDbModel
import com.raywenderlich.android.jetnotes.domain.model.ColorModel
 */
import kotlinx.coroutines.flow.StateFlow

//This class becomes single source of truth for reminders,
// we don't need to create Reminder properties anymore
class FlowRepository(private val repo: Repository){
    private val mainNotesLiveData: MutableStateFlow<List<NoteProperty>> by lazy{
        MutableStateFlow(emptyList<NoteProperty>())
    }

    private val archivedNotesLiveData: MutableStateFlow<List<NoteProperty>> by lazy{
        MutableStateFlow(emptyList<NoteProperty>())
    }

    init{
        updateNotesState()
    }

    fun getMainNotes(): StateFlow<List<NoteProperty>> = mainNotesLiveData.asStateFlow()
    fun getArchivedNotes(): StateFlow<List<NoteProperty>> = archivedNotesLiveData.asStateFlow()

    /*
    fun getAllColors(): LiveData<List<ColorModel>>{//TODO: replace this temporary color repo
        val colors = ColorDbModel.DEFAULT_COLORS
        val mapper = DbMapperImpl()
        return MutableLiveData(mapper.mapColors(colors))
    }*/

    fun deleteNote(id: String) {
        repo.deleteNote(id)
        updateNotesState()
    }

    fun saveNote(note: NoteProperty){
        repo.saveNote(note)
        updateNotesState()
    }

    fun saveNewNote(title: String, content: String, colorId: Long, canBeChecked: Boolean, editDate: String? = null){
        repo.saveNewNote(title,content,colorId, canBeChecked, editDate)
        updateNotesState()
    }

    fun archiveNote(id: String){
        repo.archiveNote(id)
        updateNotesState()
    }

    fun deleteArchivedNotes(){
        repo.deleteArchivedNotes()
        updateArchivedNotes()
    }

    fun restoreNote(id: String){
        repo.restoreNote(id)
        updateNotesState()
    }

    fun markNote(id: String) {
        repo.markNote(id)
        updateNotesState()
    }
    fun unmarkNote(id: String) {
        repo.unmarkNote(id)
        updateNotesState()
    }

    fun getNote(id: String): NoteProperty? {
        return repo.getNote(id)
    }

    fun pinNote(id: String){
        repo.pinNote(id)
        updateNotesState()
    }
    fun unpinNote(id: String){
        repo.unpinNote(id)
        updateNotesState()
    }

    private fun updateNotesState() {
        mainNotesLiveData.value = repo.getMainNotes()
        archivedNotesLiveData.value  = repo.getArchivedNotes()
    }

    private fun updateArchivedNotes() {
        archivedNotesLiveData.value  = repo.getArchivedNotes()
    }
    private fun updateMainNotes() {
        mainNotesLiveData.value = repo.getMainNotes()
    }

}
