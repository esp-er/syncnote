package com.raywenderlich.jetnotes.networking

import io.ktor.websocket.*
import java.util.concurrent.atomic.*

class Connection(val session: DefaultWebSocketSession) {
    companion object { //Ensures 2 users will never get the same id
        var lastId = AtomicInteger(0)
    }
    val name = "user${lastId.getAndIncrement()}"
}