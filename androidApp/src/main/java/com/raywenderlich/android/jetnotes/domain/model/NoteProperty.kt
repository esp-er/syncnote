package com.raywenderlich.android.jetnotes.domain.model

import com.raywenderlich.android.jetnotes.domain.UUID
import androidx.compose.ui.graphics.Color
import kotlinx.datetime.Instant


const val NEW_UUID = "NEW"

/**
 * Model class that represents one Note
 */
data class NoteProperty(
    val id: String = NEW_UUID,
    val title: String = "",
    val content: String = "",
    val colorId: Long = 0, //TODO: Create a Color representation class?
    val canBeChecked: Boolean = false,
    val isChecked: Boolean = false,
    val isArchived: Boolean = false,
    val editDate: Instant = Instant.fromEpochMilliseconds(0)
)