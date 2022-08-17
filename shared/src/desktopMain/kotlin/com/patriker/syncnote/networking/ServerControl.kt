package com.patriker.syncnote.networking
import java.util.concurrent.atomic.AtomicBoolean

object ServerControl{
    var SyncOutdated = AtomicBoolean(true)
    var PairAccept = AtomicBoolean(false)

}

