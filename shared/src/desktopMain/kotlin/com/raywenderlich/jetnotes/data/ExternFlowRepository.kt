package com.raywenderlich.jetnotes.data

import com.raywenderlich.jetnotes.domain.NoteProperty

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow


class ExternFlowRepository(private val repo: ExternRepository){
    private val syncedNotes: MutableStateFlow<List<NoteProperty>> by lazy{
        MutableStateFlow(emptyList<NoteProperty>())
    }
    init{
        updateNotesState()
    }
    fun getNotesFlow(): StateFlow<List<NoteProperty>> = syncedNotes
    /*
    fun getAllColors(): LiveData<List<ColorModel>>{//TODO: replace this temporary color cacheRepo
        val colors = ColorDbModel.DEFAULT_COLORS
        val mapper = DbMapperImpl()
        return MutableLiveData(mapper.mapColors(colors))
    }*/

    fun clearAll() {
        repo.clearAll()
        updateNotesState()
    }

    fun saveAll(notes: List<NoteProperty>){
        repo.saveAll(notes)
        updateNotesState()
    }

    fun clearAndSaveAll(notes: List<NoteProperty>){
        repo.clearAll()
        saveAll(notes)
        updateNotesState()
    }

    fun deleteNote(id: String) {
        repo.deleteNote(id)
        updateNotesState()
    }

    fun saveNote(note: NoteProperty){
        repo.saveNote(note)
        updateNotesState()
    }

    fun getNote(id: String): NoteProperty? {
        return repo.getNote(id)
    }

    private fun updateNotesState() {
        syncedNotes.value = repo.getNotes()
    }
}
