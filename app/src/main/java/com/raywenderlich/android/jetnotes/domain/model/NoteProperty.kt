package com.raywenderlich.android.jetnotes.domain.model

import androidx.compose.ui.graphics.Color
import kotlinx.datetime.Instant


const val NEW_ID = -1L

/**
 * Model class that represents one Note
 */
data class NoteProperty(
    val id: String, //primary key in db
    val title: String = "",
    val content: String = "",
    val colorId: Long, //TODO: Create a Color representation class?
    val canBeChecked: Boolean = false, // null represents that the note can't be checked off
    val isChecked: Boolean = false,
    val isArchived: Boolean = false,
    val editDate: Instant = Instant.fromEpochMilliseconds(0)
)
