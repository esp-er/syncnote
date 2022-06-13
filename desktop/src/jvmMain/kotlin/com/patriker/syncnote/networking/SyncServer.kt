package com.patriker.syncnote.networking

import com.patriker.syncnote.networking.Connection
import com.raywenderlich.jetnotes.MainViewModel
import com.raywenderlich.jetnotes.data.Repository
import com.raywenderlich.jetnotes.domain.NEW_UUID
import com.raywenderlich.jetnotes.domain.NoteProperty
import io.ktor.serialization.kotlinx.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import java.time.*
import java.util.*
import kotlin.collections.LinkedHashSet
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.response.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import io.ktor.websocket.serialization.*
import kotlinx.serialization.json.Json
import org.koin.dsl.module
import kotlin.concurrent.thread
import kotlinx.coroutines.*
import org.slf4j.*
import java.io.*

object SocketSession {
    var sess: DefaultWebSocketServerSession? = null
}

fun Application.configureSocketServer(dataSource: Repository) {
    install(WebSockets) {
        contentConverter = KotlinxWebsocketSerializationConverter(Json)
        pingPeriod = Duration.ofSeconds(15)
        timeout = Duration.ofSeconds(15)
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }

    routing {
        val connections = Collections.synchronizedSet<Connection?>(LinkedHashSet())
        webSocket("/syncnote") {
            SocketSession.sess = this
            println("Adding user!")
            val thisConnection = Connection(this)
            connections += thisConnection
            sendNotes(dataSource.getMainNotes())
            while (true) {
                ensureActive()
                if (Control.SendUpdates.getAndSet(false)) {
                    sendNotes(dataSource.getMainNotes())
                }
                delay(50)
            }
            connections -= thisConnection
            println("exiting server")
        }
    }
}

suspend fun DefaultWebSocketServerSession.sendNotes(notes: List<NoteProperty>) =
    withContext(Dispatchers.IO) {
        try {
            sendSerialized(notes)
        } catch (e: Exception) {
            if(e is CancellationException){
                throw e
            }
            println(e.localizedMessage)
        }
    }


class SyncServer(private val dataSource: Repository){
    fun start(){
        val environment = applicationEngineEnvironment {
            log = LoggerFactory.getLogger("ktor.application")
            connector {
                port = 9000
            }
            module{
                configureSocketServer(dataSource)
            }
        }

        embeddedServer(
            Netty, environment
        ).start(wait = true)
    }


}
