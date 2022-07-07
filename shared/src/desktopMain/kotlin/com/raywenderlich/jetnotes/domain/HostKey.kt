package com.raywenderlich.jetnotes.domain

data class UUIDStr(val UUID: String = "NEW")

object HostKey{
    var value: UUIDStr = UUIDStr()
}