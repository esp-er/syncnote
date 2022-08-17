package com.patriker.syncnote.data

import com.patriker.syncnote.domain.NoteProperty
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.patriker.syncnote.data.ExternRepository

/*
import com.raywenderlich.android.jetnotes.data.database.dbmapper.DbMapperImpl
import com.raywenderlich.android.jetnotes.data.database.model.ColorDbModel
import com.raywenderlich.android.jetnotes.domain.model.ColorModel
 */

//This class becomes single source of truth for reminders,
// we don't need to create Reminder properties anymore
class AndroidExternRepository(private val cacheRepo: ExternRepository){
    private val syncedNotesLiveData: MutableLiveData<List<NoteProperty>> by lazy{
        MutableLiveData<List<NoteProperty>>()
    }
    init{
        updateNotesLiveData()
    }

    fun getNotesLiveData(): LiveData<List<NoteProperty>> = syncedNotesLiveData


    /*
    fun getAllColors(): LiveData<List<ColorModel>>{//TODO: replace this temporary color cacheRepo
        val colors = ColorDbModel.DEFAULT_COLORS
        val mapper = DbMapperImpl()
        return MutableLiveData(mapper.mapColors(colors))
    }*/

    fun clearAll() {
        cacheRepo.clearAll()
        updateNotesLiveData()
    }

    fun saveAll(notes: List<NoteProperty>){
        cacheRepo.saveAll(notes)
        updateNotesLiveData()
    }

    fun clearAndSaveAll(notes: List<NoteProperty>){
        cacheRepo.clearAll()
        saveAll(notes)
        updateNotesLiveData()
    }

    fun deleteNote(id: String) {
        cacheRepo.deleteNote(id)
        updateNotesLiveData()
    }

    fun saveNote(note: NoteProperty){
        cacheRepo.saveNote(note)
        updateNotesLiveData()
    }

    fun getNote(id: String): NoteProperty? {
        return cacheRepo.getNote(id)
    }

    private fun updateNotesLiveData() {
        syncedNotesLiveData.postValue(cacheRepo.getNotes())
    }

}