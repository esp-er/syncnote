package com.patriker.syncnote.networking

import androidx.compose.runtime.collectAsState
import com.patriker.syncnote.MainViewModel
import com.patriker.syncnote.domain.*
import com.patriker.syncnote.debug.DebugBuild
import io.ktor.serialization.*
import io.ktor.serialization.kotlinx.*
import io.ktor.server.websocket.*
import java.time.*
import kotlin.collections.LinkedHashSet
import io.ktor.server.routing.*
//import io.ktor.http.*
import io.ktor.server.application.*
//import io.ktor.server.cio.*
import io.ktor.server.netty.*

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
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.json.Json
import kotlin.coroutines.EmptyCoroutineContext

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
data class PairingResult(val Paired: Boolean, val deviceName: String)

class SyncServer(private val viewModel: MainViewModel, private val pairingInitial: Boolean = false,
                 private val deviceInitial: String = "Unknown"){

    private var _clientWishesToPair = MutableStateFlow(false)
    val clientWishesToPair: StateFlow<Boolean> = _clientWishesToPair
    private var _clientData = MutableStateFlow(PairingData("", ""))
    var clientData = _clientData.asStateFlow()

    private val _isSyncingLive = MutableStateFlow(false)
    val isSyncingLive: StateFlow<Boolean> = _isSyncingLive
    private val _pairingResult = MutableStateFlow(PairingResult(pairingInitial, deviceInitial))
    val pairingResult: StateFlow<PairingResult> = _pairingResult
    private val _deviceName = MutableStateFlow(deviceInitial)
    val deviceName: StateFlow<String> = _deviceName

    private val notesToSend: StateFlow<List<NoteProperty>> by lazy { viewModel.notes }

    private lateinit var sendJob: Job
    private lateinit var receiveJob: Job


    private lateinit var serverEngine: ApplicationEngine

    val receivedNotes: MutableStateFlow<List<NoteProperty>> by lazy {
        MutableStateFlow(emptyList())
    }

    suspend fun clientConnects(clientInfo: PairingData){
        println("Client wishes to pair!")
        _deviceName.value = clientInfo.deviceName
        _clientWishesToPair.value = true
        _clientData.value = clientInfo

        println("waiting for pair accept........")
        while(!ServerControl.PairAccept.getAndSet(false)){
            delay(10)
        }
        //if already paired just set is syncing
        //if clientData is OK and Host user interactively clicks ok
        //viewModel.setPairedState(true) //perhaps pass client data here
        _clientWishesToPair.value = false
        _pairingResult.value = PairingResult(true, clientInfo.deviceName)
        println("Accepted pairing")
        //TODO: Receiver an UUID scanned from the QR Code
        //Validate the UUID and add a StateFlow that communicates
        //to ViewModel that a Client wishes to Pair with this computer
    }

    fun updateReceivedNotes(notes: List<NoteProperty>){
        receivedNotes.value = notes
    }
    /*

    fun testStart(){
        embeddedServer(Netty, port = 8000){
            configureRouting()
        }.start(wait=true)
    }
     */

    fun stop(){
        if(this::serverEngine.isInitialized) {
            sendJob.cancel()
            receiveJob.cancel()
            val tmp = serverEngine.stopServerOnCancellation()
            tmp.cancel()
            serverEngine.stop(100, 200)
        }
    }
    fun start(listenAddr: String = "0.0.0.0", listenPort: Int = 9000){
        val environment = applicationEngineEnvironment {
            //log = LoggerFactory.getLogger("ktor.application")
            connector {
                host = listenAddr
                port = listenPort
            }
            module{
                //configureRouting()
                configureSocketServer()
            }
        }

        serverEngine = embeddedServer(
            Netty, environment
        ).start(wait = true)
    }

    fun Application.configureSocketServer() {
        install(WebSockets) {
            contentConverter = KotlinxWebsocketSerializationConverter(Json)
            pingPeriod = Duration.ofSeconds(15)
            timeout = Duration.ofSeconds(30)
            maxFrameSize = Long.MAX_VALUE
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
                if(!pairingInitial) {
                    val (validateResult, data) = validatePairing()
                    //TODO: we need a client indication to whether we want to pair
                    clientConnects(data)//do this after next step instead?
                    sendPairingResult(PairingResponse(validateResult, data.pairingMessage))
                }

                if(_pairingResult.value.Paired) {
                    viewModel.setSyncingState(true)
                    _isSyncingLive.value = true
                    println("Now sending Notes!")
                    sendJob = launch {
                        while (true) {
                            ensureActive()
                            if (ServerControl.SyncOutdated.getAndSet(false))
                                sendNotes(notesToSend.value)
                            delay(25)
                        }
                    }
                    receiveJob = launch {
                        while (true) {
                            ensureActive()
                            receiveNotes()
                            delay(25)
                        }
                    }
                    sendJob.join()
                    receiveJob.join()
                }
                connections -= thisConnection
                viewModel.setSyncingState(false)
                println("exiting server")
            }
        }
    }
    suspend fun DefaultWebSocketServerSession.sendPairingResult(pairResp: PairingResponse) = withContext(Dispatchers.IO) {
        try {
            sendSerialized(pairResp)
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

        println(pairingData.pairingMessage)

        if(pairingData.deviceName.isNotBlank()){
            println("device connect: ${pairingData.deviceName}")
        }

        if(pairingData.pairingMessage.isNotBlank()){
            println("code: ${pairingData.pairingMessage}")
        }
        //Validate here mock
        println("HOSTKEY: ${HostKey.value}")
        println("LAUNCH DECRYPT")

        var valid = false
        if(!DebugBuild.DEBUG) {
            val decryptedMessage = CryptUtil.decrypt(pairingData.pairingMessage, HostKey.value)
            valid = decryptedMessage == "startsync"
        }else{
            valid = true
        }

        println("Pairing Message (encrypted)!: ${pairingData.pairingMessage}")
        //println("Decrypted!: $decryptedMessage")
        return PairingValidationData(valid, pairingData)
    }


    suspend fun DefaultWebSocketServerSession.sendNotes(notes: List<NoteProperty>) =
        withContext(Dispatchers.IO) {
            try {
                sendSerialized(notes)
            } catch (e: Exception) {
                if (e is CancellationException) {
                    throw e
                }
                println(e.localizedMessage)
            }
        }


    suspend fun DefaultWebSocketServerSession.receiveNotes() =
        withContext(Dispatchers.IO) {
            try {
                val networkNotes = receiveDeserialized<List<NoteProperty>>()
                updateReceivedNotes(networkNotes)
                println("Received notes from client!!")
            } catch (e: Exception) {
                if (e is WebsocketDeserializeException) {
                    throw e
                }
                if (e is ClosedReceiveChannelException) {
                    throw e
                }
            }
        }

}
