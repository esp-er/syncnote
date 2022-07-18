package com.patriker.syncnote.networking

import com.raywenderlich.jetnotes.domain.NoteProperty
import com.raywenderlich.jetnotes.domain.PairingData
import com.raywenderlich.jetnotes.domain.PairingResponse
import io.ktor.serialization.kotlinx.*
import io.ktor.server.websocket.*
import java.time.*
import java.util.*
import kotlin.collections.LinkedHashSet
import io.ktor.server.routing.*
//import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.cio.*

import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.websocket.WebSockets

/*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.cio.*
import io.ktor.server.routing.*
import io.ktor.server.response.*
import io.ktor.server.request.*
 */
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.json.Json

object SocketSession {
    var sess: DefaultWebSocketServerSession? = null
}


/*
fun Application.configureRouting(){
    routing {
        get("/") {
            call.respondText("hej")
        }
    }

}*/



fun Application.configureSocketServer( onClientConnect: () -> Unit) {
    install(WebSockets) {
        contentConverter = KotlinxWebsocketSerializationConverter(Json)
        pingPeriod = Duration.ofSeconds(15)
        timeout = Duration.ofSeconds(30)
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
            ///sendNotes(dataSource.getMainNotes())
            val validateResult = validatePairing() //TODO: add logic to validate pairing data in this function

            sendPairingResult(PairingResponse(validateResult))

            if(validateResult) {
                onClientConnect()
                //TODO: Receiver an UUID scanned from the QR Code
                //Validate the UUID and add a StateFlow that communicates
                //to ViewModel that a Client wishes to Pair with this computer
                while (true) {
                    ensureActive()
                    if (ServerControl.SendUpdates.getAndSet(false)) {
                        //sendNotes(dataSource.getMainNotes())
                    }
                    delay(50)
                }
            }
            connections -= thisConnection
            println("exiting server")
        }
    }
}
suspend fun DefaultWebSocketServerSession.sendPairingResult(b: PairingResponse) = withContext(Dispatchers.IO) {
    try {
        sendSerialized(b)
    }
    catch(e: Exception){
        e.printStackTrace()
        println("failed to send pairing result to client!!")
    }
}

suspend fun DefaultWebSocketServerSession.validatePairing(): Boolean {
    val request =
        try {
            val data = receiveDeserialized<PairingData>()
            data
        } catch (e: Exception){
            PairingData("", "")
        }

    if(request.deviceName.isNotBlank()){
        println("device connect: ${request.deviceName}")
    }

    if(request.pairingCode.isNotBlank()){
        println("code: ${request.pairingCode}")
    }
    return true
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


class SyncServer(){
    private var clientWishesToPair: MutableStateFlow<Boolean> = MutableStateFlow(false)

    fun clientPairFlow() = clientWishesToPair.asStateFlow()
    fun clientConnects(){
        println("Client wishes to pair!")
        clientWishesToPair.value = true
    }

    /*

    fun testStart(){
        embeddedServer(Netty, port = 8000){
            configureRouting()
        }.start(wait=true)
    }
     */



    fun start(){
        val environment = applicationEngineEnvironment {
            //log = LoggerFactory.getLogger("ktor.application")
            connector {
                port = 8000
                host = "0.0.0.0"
            }
            module{
                //configureRouting()
                configureSocketServer(::clientConnects)
            }
        }

        embeddedServer(
            CIO, environment
        ).start(wait = true)
    }




}
