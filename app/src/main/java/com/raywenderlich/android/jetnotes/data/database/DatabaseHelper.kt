package com.raywenderlich.android.jetnotes.data.database


import com.raywenderlich.android.jetnotes.OpenNotesDb
import com.raywenderlich.android.jetnotes.db.NotePropertyDb
import com.squareup.sqldelight.db.SqlDriver

internal fun Boolean.toLong(): Long = if (this) 1L else 0L

class DatabaseHelper(
    sqlDriver: SqlDriver,
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
            .insertNote(
                note.id,
                note.title,
                note.content,
                note.colorId,
                note.canBeChecked,
                note.isChecked,
                note.isArchived,
                note.editDate
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




    /*
    fun insertReminder(id: String, title: String) {
        dbRef.tableQueries.insertReminder(id, title)
    }

    fun deleteReminder(id: String) {
        dbRef.tableQueries.deleteReminder(id)
    }

    fun updateIsCompleted(id: String, isCompleted: Boolean) {
        dbRef.tableQueries
            .updateIsCompleted(isCompleted.toLong(), id)
    }

 */
    //TODO: implement all other db operations inside of Repository.kt
    //Or NotesTable.sq


}