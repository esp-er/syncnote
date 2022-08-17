package com.patriker.syncnote.data

import com.patriker.syncnote.domain.NoteProperty
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.patriker.syncnote.data.Repository

/*
import com.raywenderlich.android.jetnotes.data.database.dbmapper.DbMapperImpl
import com.raywenderlich.android.jetnotes.data.database.model.ColorDbModel
import com.raywenderlich.android.jetnotes.domain.model.ColorModel
 */

//This class becomes single source of truth for reminders,
// we don't need to create Reminder properties anymore
class AndroidRepository(private val repo: Repository){
    private val mainNotesLiveData: MutableLiveData<List<NoteProperty>> by lazy{
        MutableLiveData<List<NoteProperty>>()
    }

    private val archivedNotesLiveData: MutableLiveData<List<NoteProperty>> by lazy{
        MutableLiveData<List<NoteProperty>>()
    }
    init{
        updateNotesLiveData()
    }

    fun getMainNotes(): LiveData<List<NoteProperty>> = mainNotesLiveData
    fun getArchivedNotes(): LiveData<List<NoteProperty>> = archivedNotesLiveData

    /*
    fun getAllColors(): LiveData<List<ColorModel>>{//TODO: replace this temporary color repo
        val colors = ColorDbModel.DEFAULT_COLORS
        val mapper = DbMapperImpl()
        return MutableLiveData(mapper.mapColors(colors))
    }*/

    fun deleteNote(id: String) {
        repo.deleteNote(id)
        updateNotesLiveData()
    }

    fun saveNote(note: NoteProperty){
        repo.saveNote(note)
        updateNotesLiveData()
    }

    fun saveNewNote(title: String, content: String, colorId: Long, canBeChecked: Boolean, editDate: String? = null){
        repo.saveNewNote(title,content,colorId, canBeChecked, editDate)
        updateNotesLiveData()
    }

    fun archiveNote(id: String){
        repo.archiveNote(id)
        updateNotesLiveData()
    }

    fun restoreNote(id: String){
        repo.restoreNote(id)
        updateNotesLiveData()
    }

    fun markNote(id: String) {
        repo.markNote(id)
        updateNotesLiveData()
    }
    fun unmarkNote(id: String) {
        repo.unmarkNote(id)
        updateNotesLiveData()
    }

    fun getNote(id: String): NoteProperty? {
        return repo.getNote(id)
    }

    fun pinNote(id: String){
        repo.pinNote(id)
        updateNotesLiveData()
    }
    fun unpinNote(id: String){
        repo.unpinNote(id)
        updateNotesLiveData()
    }

    private fun updateNotesLiveData() {
        mainNotesLiveData.postValue(repo.getMainNotes())
        archivedNotesLiveData.postValue(repo.getArchivedNotes())
    }

}
