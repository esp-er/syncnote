package com.patriker.syncnote.networking

import com.raywenderlich.jetnotes.data.Repository
import com.raywenderlich.jetnotes.domain.NoteProperty
import io.ktor.serialization.kotlinx.*
import io.ktor.server.websocket.*
import java.time.*
import java.util.*
import kotlin.collections.LinkedHashSet
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.slf4j.*

object SocketSession {
    var sess: DefaultWebSocketServerSession? = null
}

fun Application.configureSocketServer(dataSource: Repository, clientPairFlag: MutableStateFlow<Boolean>) {
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
            /*
            Check if server already does not have a client paired currently
            if (receivedString == HostKey.value.UUID.toString){
             clientPairFlag.value = true
            }
             */
            val thisConnection = Connection(this)
            connections += thisConnection
            sendNotes(dataSource.getMainNotes())
            clientPairFlag.value = true

            //TODO: Receiver an UUID scanned from the QR Code
            //Validate the UUID and add a StateFlow that communicates
            //to ViewModel that a Client wishes to Pair with this computer
            while (true) {
                ensureActive()
                if (ServerControl.SendUpdates.getAndSet(false)) {
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
    private var clientWishesToPair: MutableStateFlow<Boolean> = MutableStateFlow(false)

    fun clientPairFlow() = clientWishesToPair.asStateFlow()

    fun start(){
        val environment = applicationEngineEnvironment {
            log = LoggerFactory.getLogger("ktor.application")
            connector {
                port = 9000
            }
            module{
                configureSocketServer(dataSource, clientWishesToPair)
            }
        }

        embeddedServer(
            Netty, environment
        ).start(wait = true)
    }




}
