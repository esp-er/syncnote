package com.patriker.syncnote.data

import java.util.concurrent.atomic.AtomicBoolean

object ClientControl {
    var SendUpdates = AtomicBoolean(true)
}