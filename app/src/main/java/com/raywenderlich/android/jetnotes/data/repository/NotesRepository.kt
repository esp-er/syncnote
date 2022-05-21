package com.raywenderlich.android.jetnotes.data.repository


import com.raywenderlich.android.jetnotes.OpenNotesDb
import com.raywenderlich.android.jetnotes.domain.UUID
import com.raywenderlich.android.jetnotes.domain.model.NoteProperty
//import com.raywenderlich.android.jetnotes.data.database.NotePropertyDb
import com.raywenderlich.android.jetnotes.data.database.DatabaseHelper
import com.raywenderlich.android.jetnotes.db.NotePropertyDb
import com.raywenderlich.android.jetnotes.ui.components.Note
import kotlinx.datetime.toInstant
import kotlinx.datetime.Clock
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.raywenderlich.android.jetnotes.data.database.toLong
import kotlinx.datetime.Instant

import com.raywenderlich.android.jetnotes.data.database.dbmapper.DbMapperImpl
import com.raywenderlich.android.jetnotes.data.database.model.ColorDbModel
import com.raywenderlich.android.jetnotes.domain.model.ColorModel
import com.raywenderlich.android.jetnotes.domain.model.NEW_UUID

fun NotePropertyDb.isChecked(): Boolean = this.isChecked != 0L
fun NotePropertyDb.canBeChecked(): Boolean = this.canBeChecked != 0L
fun NotePropertyDb.isArchived(): Boolean = this.isArchived != 0L


//This class becomes single source of truth for reminders,
// we don't need to create Reminder properties anymore
class NotesRepository(
    private val databaseHelper: DatabaseHelper
) {
    private val allNotes: List<NoteProperty>
        get() = databaseHelper.fetchAllNotes().map(NotePropertyDb::toNoteProperty)

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

    fun getAllColors(): LiveData<List<ColorModel>>{//TODO: replace this temporary color repo
        val colors = ColorDbModel.DEFAULT_COLORS
        val mapper = DbMapperImpl()
        return MutableLiveData(mapper.mapColors(colors))
    }

    fun deleteNote(id: String) {
        databaseHelper.removeNote(id)
        updateNotesLiveData()
    }

    fun saveNote(note: NoteProperty){
        val newId = if(note.id == NEW_UUID) UUID().toString() else note.id
        databaseHelper.insert(
            NotePropertyDb(
                id = newId,
                title = note.title,
                content = note.content,
                colorId = note.colorId,
                canBeChecked = note.canBeChecked.toLong(),
                isChecked = note.isChecked.toLong(),
                isArchived = note.isArchived.toLong(),
                editDate = note.editDate.toString()
            )
        )
        updateNotesLiveData()
    }

    fun saveNewNote(title: String, content: String, colorId: Long, canBeChecked: Boolean, editDate: String? = null){
        databaseHelper.insert(
            NotePropertyDb(
                id = UUID().toString(),
                title = title,
                content = content,
                colorId = colorId,
                canBeChecked = canBeChecked.toLong(),
                isChecked = 0,
                isArchived = 0,
                editDate = editDate ?: Clock.System.now().toString())
        )
        updateNotesLiveData()
    }

    fun archiveNote(id: String){
        databaseHelper.archiveNote(id)
        updateNotesLiveData()
    }

    fun restoreNote(id: String){
        databaseHelper.unarchiveNote(id)
        updateNotesLiveData()
    }

    fun markNote(id: String) {
        databaseHelper.markNote(id)
        updateNotesLiveData()
    }
    fun unmarkNote(id: String) {
        databaseHelper.unmarkNote(id)
        updateNotesLiveData()
    }

    fun getNote(id: String): NoteProperty? {
        return databaseHelper.fetchNote(id)?.toNoteProperty()
    }

    private fun updateNotesLiveData() {
        mainNotesLiveData.postValue(databaseHelper.fetchMainNotes().map(NotePropertyDb::toNoteProperty))
        archivedNotesLiveData.postValue(databaseHelper.fetchArchivedNotes().map(NotePropertyDb::toNoteProperty))
    }

}

fun NotePropertyDb.toNoteProperty() = NoteProperty(
    id = this.id,
    title = this.title ?: "",
    content = this.content ?: "",
    colorId = 0L,
    canBeChecked = this.canBeChecked(),
    isChecked = this.isChecked(),
    isArchived = this.isArchived(),
    editDate = Instant.parse(this.editDate)
)
