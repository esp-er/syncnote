package com.raywenderlich.jetnotes.networking
import java.util.concurrent.atomic.AtomicBoolean

object ServerControl{
    var SyncOutdated = AtomicBoolean(true)
}