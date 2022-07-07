package com.patriker.syncnote.networking
import java.util.concurrent.atomic.AtomicBoolean

object ServerControl{
    var SendUpdates = AtomicBoolean(false)
}