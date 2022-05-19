package com.raywenderlich.android.jetnotes.domain
import java.util.UUID

class UUID {
    private val value: UUID = UUID.randomUUID() //Note: remember how UUIDs can be created on the JVM
    override fun toString() = value.toString()
}

/*TODO: MPP
actual class UUID actual constructor() {
  private val value: UUID = UUID.randomUUID() //Note: remember how UUIDs can be created on the JVM
  actual override fun toString() = value.toString()
}
 */