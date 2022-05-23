package com.raywenderlich.jetnotes.domain

expect class UUID() {
    override fun toString(): String
}

/*
//TODO: MPP
actual class UUID actual constructor() {
  private val value: UUID = UUID.randomUUID() //Note: remember how UUIDs can be created on the JVM
  actual override fun toString() = value.toString()
}
*/