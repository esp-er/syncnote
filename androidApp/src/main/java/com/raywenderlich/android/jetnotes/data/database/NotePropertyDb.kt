package com.raywenderlich.android.jetnotes.data.database

data class NotePropertyDb(
    val id: String, //primary key in db
    val title: String,
    val content: String,
    val colorId: Long?,
    val canBeChecked: Long,
    val isChecked: Long,
    val isArchived: Long,
    val editDate: String?
) {

    //TODO: complete toString implementation
    public override fun toString(): String = """ 
  | NotePropertyDb[
  |  id: $id
  |  title: $title
  |  content: $content
  |  editDate: $editDate
  |]
  """.trimMargin()
}
