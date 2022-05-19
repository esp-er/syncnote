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

fun NotePropertyDb.isChecked(): Boolean = this.isChecked != 0L
fun NotePropertyDb.canBeChecked(): Boolean = this.canBeChecked != 0L
fun NotePropertyDb.isArchived(): Boolean = this.isArchived != 0L


//This class becomes single source of truth for reminders,
// we don't need to create Reminder properties anymore
class NotesRepository(
    private val databaseHelper: DatabaseHelper
) {
    val allNotes: List<NoteProperty>
        get() = databaseHelper.fetchAllNotes().map(NotePropertyDb::toNoteProperty)

    val mainNotes: LiveData<List<NoteProperty>> by lazy{
        MutableLiveData(databaseHelper.fetchMainNotes().map(NotePropertyDb::toNoteProperty))
    }

    val archivedNotes: LiveData<List<NoteProperty>> by lazy{
        MutableLiveData(databaseHelper.fetchArchivedNotes().map(NotePropertyDb::toNoteProperty))
    }

    fun deleteNote(id: String) {
        databaseHelper.removeNote(id)
    }

    fun createNote(title: String, content: String, colorId: Long,
                   canBeChecked: Boolean, editDate: String?){
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

    fun getNote(id: String): NoteProperty? {
        return databaseHelper.fetchNote(id)?.toNoteProperty()
    }



    /*
      fun markReminder(id: String, isCompleted: Boolean) {
          databaseHelper.updateIsCompleted(id, isCompleted)
      }*/

}

fun NotePropertyDb.toNoteProperty() = NoteProperty(
    id = this.id,
    title = this.title ?: "",
    content = this.content ?: "",
    colorId = 0L,
    canBeChecked = this.canBeChecked(),
    isChecked = this.isChecked(),
    isArchived = this.isArchived(),
    editDate = this.editDate.toInstant()
)
