package com.raywenderlich.jetnotes.data


import com.raywenderlich.jetnotes.domain.UUID
import com.raywenderlich.jetnotes.domain.NoteProperty
import com.raywenderlich.jetnotes.data.DatabaseHelper
import com.raywenderlich.jetnotes.db.NotePropertyDb
import kotlinx.datetime.Clock
import com.raywenderlich.jetnotes.data.toLong
import com.raywenderlich.jetnotes.domain.NEW_UUID
import kotlinx.datetime.Instant


//This class becomes single source of truth for reminders,
// we don't need to create Reminder properties anymore
class ExternRepository(
    private val databaseHelper: ExternDatabaseHelper
) {
    private val syncedNotes: List<NoteProperty>
        get() = databaseHelper.fetchAllNotes().map(NotePropertyDb::toNoteProperty)

    fun getNotes(): List<NoteProperty> = syncedNotes


    fun deleteNote(id: String) {
        databaseHelper.removeNote(id)
    }

    fun saveAll(notes: List<NoteProperty>){
        notes.forEach{ note -> saveNote(note) }
    }

    fun clearAll(){
        databaseHelper.clearAll()
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
    }

    fun getNote(id: String): NoteProperty? {
        return databaseHelper.fetchNote(id)?.toNoteProperty()
    }

}
