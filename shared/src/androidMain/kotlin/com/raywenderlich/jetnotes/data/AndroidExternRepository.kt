package com.raywenderlich.jetnotes.data

import android.provider.ContactsContract
import com.raywenderlich.jetnotes.data.DatabaseHelper
import com.raywenderlich.jetnotes.data.toLong
import com.raywenderlich.jetnotes.db.NotePropertyDb

import com.raywenderlich.jetnotes.domain.UUID
import com.raywenderlich.jetnotes.data.Repository
import com.raywenderlich.jetnotes.domain.NoteProperty
import kotlinx.datetime.Clock
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.raywenderlich.jetnotes.OpenNotesDb
import kotlinx.datetime.Instant

/*
import com.raywenderlich.android.jetnotes.data.database.dbmapper.DbMapperImpl
import com.raywenderlich.android.jetnotes.data.database.model.ColorDbModel
import com.raywenderlich.android.jetnotes.domain.model.ColorModel
 */
import com.raywenderlich.jetnotes.domain.NEW_UUID

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