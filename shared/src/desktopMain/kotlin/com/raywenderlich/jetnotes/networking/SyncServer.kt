package com.raywenderlich.jetnotes.networking

import com.raywenderlich.jetnotes.MainViewModel
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





data class PairingValidationData(val valid: Boolean, val pairingData: PairingData)

class SyncServer(private val viewModel: MainViewModel){
    private var clientWishesToPair: MutableStateFlow<Boolean> = MutableStateFlow(false)
    private var clientData: MutableStateFlow<PairingData> = MutableStateFlow(PairingData("", ""))

    fun clientPairFlow() = clientWishesToPair.asStateFlow()
    fun clientConnects(clientInfo: PairingData){
        println("Client wishes to pair!")
        clientWishesToPair.value = true
        clientData.value = clientInfo
        //if already paired just set is syncing
        //if clientData is OK and Host user interactively clicks ok
        viewModel.setPairedState(true) //perhaps pass client data here
        println("Set paired true")
        //TODO: Receiver an UUID scanned from the QR Code
        //Validate the UUID and add a StateFlow that communicates
        //to ViewModel that a Client wishes to Pair with this computer
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
                port = 9000
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

    fun Application.configureSocketServer( onClientConnect: (PairingData) -> Unit) {
        install(WebSockets) {
            contentConverter = KotlinxWebsocketSerializationConverter(Json)
            pingPeriod = java.time.Duration.ofSeconds(15)
            timeout = java.time.Duration.ofSeconds(30)
            maxFrameSize = kotlin.Long.MAX_VALUE
            masking = false
        }

        routing {
            val connections = java.util.Collections.synchronizedSet<Connection?>(LinkedHashSet())
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
                val (validateResult, data) = validatePairing() //TODO: add logic to validate pairing data in this function

                sendPairingResult(PairingResponse(validateResult))

                if(validateResult) {
                    onClientConnect(data)
                    viewModel.setSyncingState(true)
                    println("Now sending Notes!")
                    while (true) {
                        ensureActive()
                        //if (ServerControl.SyncOutdated.getAndSet(false)) { //Set this flag whenever notes are out of date
                            sendNotes(viewModel.notes.value) //TODO: add observability and check if notes changed first
                            println("server sending ${viewModel.notes.value.size} notes")
                        //}
                        delay(25)
                    }
                }
                connections -= thisConnection
                viewModel.setSyncingState(false)
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

    suspend fun DefaultWebSocketServerSession.validatePairing(): PairingValidationData {
        println("start pair")
        val pairingData =
            try {
                val data = receiveDeserialized<PairingData>()
                data
            } catch (e: Exception){
                println("exception on pairing!!!")
                PairingData("", "")
            }

        println(pairingData.pairingCode)

        if(pairingData.deviceName.isNotBlank()){
            println("device connect: ${pairingData.deviceName}")
        }

        if(pairingData.pairingCode.isNotBlank()){
            println("code: ${pairingData.pairingCode}")
        }
        //Validate here mock
        val valid = if(pairingData.pairingCode == "ASDFQWER") true else false
        println("CODE!!!: ${pairingData.pairingCode}")
        return PairingValidationData(valid, pairingData)
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

}
