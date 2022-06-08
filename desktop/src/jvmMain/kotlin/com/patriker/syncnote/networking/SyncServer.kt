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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.yield
import kotlinx.serialization.json.Json
import org.koin.dsl.module
import kotlin.concurrent.thread
import io.ktor.network.tls.certificates.*
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
                if (Control.SendUpdates.getAndSet(false)) {
                    sendNotes(dataSource.getMainNotes())
                }
                delay(100)
            }
            connections -= thisConnection
            println("exiting server")
        }
    }
}

suspend fun DefaultWebSocketServerSession.sendNotes(notes: List<NoteProperty>) {
    try {
        sendSerialized(notes)
    } catch (e: Exception) {
        println(e.localizedMessage)
        return
    }
}

fun Application.hello() {
    routing {
        get("/hello") {
            call.respondText("Hello, world!")
        }
    }
}

class SyncServer(private val dataSource: Repository){
    fun start(){
        val keyStoreFile = File("build/keystore.jks")
        val keystore = generateCertificate(
            file = keyStoreFile,
            keyAlias = "sampleAlias",
            keyPassword = "foobar",
            jksPassword = "foobar"
        )

        val environment = applicationEngineEnvironment {
            log = LoggerFactory.getLogger("ktor.application")
            connector {
                port = 9000
            }
            sslConnector(
                keyStore = keystore,
                keyAlias = "sampleAlias",
                keyStorePassword = { "foobar".toCharArray() },
                privateKeyPassword = { "foobar".toCharArray() }) {
                port = 443
                keyStorePath = keyStoreFile
            }
            module{
                //configureSocketServer(dataSource)
                hello()
            }
        }

        embeddedServer(
            Netty, environment
        ).start(wait = true)
    }


}
