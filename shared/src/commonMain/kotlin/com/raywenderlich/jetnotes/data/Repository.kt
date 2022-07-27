package com.raywenderlich.jetnotes.data


import com.raywenderlich.jetnotes.domain.UUID
import com.raywenderlich.jetnotes.domain.NoteProperty
import com.raywenderlich.jetnotes.data.DatabaseHelper
import com.raywenderlich.jetnotes.db.NotePropertyDb
import kotlinx.datetime.Clock
import com.raywenderlich.jetnotes.data.toLong
import com.raywenderlich.jetnotes.domain.NEW_UUID
import kotlinx.datetime.Instant

fun NotePropertyDb.isChecked(): Boolean = this.isChecked != 0L
fun NotePropertyDb.canBeChecked(): Boolean = this.canBeChecked != 0L
fun NotePropertyDb.isArchived(): Boolean = this.isArchived != 0L
fun NotePropertyDb.isPinned(): Boolean = this.isPinned != 0L


//This class becomes single source of truth for reminders,
// we don't need to create Reminder properties anymore
class Repository(
    private val databaseHelper: DatabaseHelper
) {
    private val allNotes: List<NoteProperty>
        get() = databaseHelper.fetchAllNotes().map(NotePropertyDb::toNoteProperty)

    private val mainNotesData: List<NoteProperty>
        get() = databaseHelper.fetchMainNotes().map(NotePropertyDb::toNoteProperty)

    private val archivedNotesData: List<NoteProperty>
        get() = databaseHelper.fetchArchivedNotes().map(NotePropertyDb::toNoteProperty)

    fun getMainNotes(): List<NoteProperty> = mainNotesData
    fun getArchivedNotes(): List<NoteProperty> = archivedNotesData

    fun deleteNote(id: String) {
        databaseHelper.removeNote(id)
    }

    fun deleteArchivedNotes(){
        databaseHelper.clearArchive()
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
                editDate = note.editDate.toString(),
                isPinned = note.isPinned.toLong()
            )
        )
    }

    fun saveNewNote(title: String, content: String, colorId: Long, canBeChecked: Boolean, editDate: String? = null){
        databaseHelper.insert(
            NotePropertyDb(
                id = UUID().toString(),
                title = title,
                content = content,
                colorId = colorId,
                canBeChecked = canBeChecked.toLong(),
                isChecked = 0L,
                isArchived = 0L,
                editDate = editDate ?: Clock.System.now().toString(),
                isPinned = 0L
            )
        )
    }

    fun archiveNote(id: String){
        databaseHelper.archiveNote(id)
    }


    fun restoreNote(id: String){
        databaseHelper.unarchiveNote(id)
    }

    fun markNote(id: String) {
        databaseHelper.markNote(id)
    }
    fun unmarkNote(id: String) {
        databaseHelper.unmarkNote(id)
    }
    fun pinNote(id:String){
        databaseHelper.pinNote(id)
    }
    fun unpinNote(id:String){
        databaseHelper.unpinNote(id)
    }

    fun getNote(id: String): NoteProperty? {
        return databaseHelper.fetchNote(id)?.toNoteProperty()
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
    editDate = Instant.parse(this.editDate),
    isPinned = this.isPinned()
)
