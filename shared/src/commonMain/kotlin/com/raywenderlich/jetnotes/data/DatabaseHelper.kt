package com.raywenderlich.jetnotes.data


import com.raywenderlich.jetnotes.OpenNotesDb
import com.raywenderlich.jetnotes.db.NotePropertyDb //Generated by sqlDelight
import com.squareup.sqldelight.db.SqlDriver

fun Boolean.toLong(): Long = if (this) 1L else 0L

class DatabaseHelper(
    private val sqlDriver: SqlDriver
) {
    private val dbRef: OpenNotesDb = OpenNotesDb(sqlDriver)

    fun fetchAllNotes(): List<NotePropertyDb> =
        dbRef.notesTableQueries
            .getAllNotes()
            .executeAsList()

    fun fetchNote(id: String): NotePropertyDb? =
        dbRef.notesTableQueries.getNote(id)
            .executeAsOneOrNull()


    fun fetchMainNotes(): List<NotePropertyDb> =
        dbRef.notesTableQueries
            .getNotes()
            .executeAsList()


    fun fetchArchivedNotes(): List<NotePropertyDb> =
        dbRef.notesTableQueries
            .getArchivedNotes()
            .executeAsList()


    fun insert(note: NotePropertyDb){
        dbRef.notesTableQueries
            .insertUpdateNote(
                note.id,
                note.title,
                note.content,
                note.colorId,
                note.canBeChecked,
                note.isChecked,
                note.isArchived,
                note.editDate,
                note.isPinned
            )
    }

    fun markNote(id: String){
        dbRef.notesTableQueries.checkNote(id)
    }
    fun unmarkNote(id: String){
        dbRef.notesTableQueries.uncheckNote(id)
    }

    fun removeNote(id: String) {
        dbRef.notesTableQueries.deleteNote(id)
    }

    fun archiveNote(id: String){
        dbRef.notesTableQueries.archiveNote(id)
    }
    fun unarchiveNote(id: String){
        dbRef.notesTableQueries.unarchiveNote(id)
    }

    fun pinNote(id: String){
        dbRef.notesTableQueries.pinNote(id)
    }

    fun unpinNote(id: String){
        dbRef.notesTableQueries.unpinNote(id)
    }



}