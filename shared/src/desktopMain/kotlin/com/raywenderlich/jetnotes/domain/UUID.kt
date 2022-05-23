package com.raywenderlich.jetnotes.domain

import java.util.UUID

actual class UUID actual constructor() {
    private val value: UUID = UUID.randomUUID() //Note: remember how UUIDs can be created on the JVM
    actual override fun toString() = value.toString()
}