package com.raywenderlich.jetnotes.domain

import kotlinx.serialization.*
import kotlinx.datetime.Instant

const val NEW_UUID = "NEW"

/**
 * Model class that represents one Note
 */
@Serializable
data class NoteProperty(
    val id: String = NEW_UUID,
    val title: String = "",
    val content: String = "",
    val colorId: Long = 0, //TODO: Create a Color representation class?
    val canBeChecked: Boolean = false,
    val isChecked: Boolean = false,
    val isArchived: Boolean = false,
    val editDate: Instant = Instant.fromEpochMilliseconds(0),
    val isPinned: Boolean = false
)
