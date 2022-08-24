package com.patriker.syncnote.data

import android.util.Log
import com.patriker.syncnote.MainViewModel
import com.patriker.syncnote.data.network.HostData
import com.patriker.syncnote.domain.CryptUtil
import com.patriker.syncnote.domain.NoteProperty
import com.patriker.syncnote.domain.PairingData
import com.patriker.syncnote.domain.PairingResponse
import io.ktor.client.*
import io.ktor.client.plugins.websocket.*
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.http.*
import io.ktor.serialization.*
import io.ktor.serialization.kotlinx.*
import io.ktor.websocket.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.json.Json
import java.net.ConnectException


class SyncClient(val viewModel: MainViewModel, var host: HostData,
                 var pairingDone: Boolean = false,
                 var pairingData: PairingData = PairingData("xiaomi note 10", "")
                 ) {
    //TODO: this class is not a view so shouldnt hold viewmodel or depend on it

    private val _isSyncingLive = MutableStateFlow(false)
    val isSyncingLive  = _isSyncingLive.asStateFlow()
    //private val _isPairingDone = MutableStateFlow(pairingDone)
    private val _isPairingDone = MutableStateFlow(pairingDone)
    val isPairingDone = _isPairingDone.asStateFlow()

    val client = HttpClient {
        install(WebSockets){
            contentConverter = KotlinxWebsocketSerializationConverter(Json)
        }
    }

    //private var noteList: List<NoteProperty> = listOf<NoteProperty>()

    suspend fun connect() = withContext(Dispatchers.IO){
        if(host.address != "0.0.0.0"){
                try {
                    client.webSocket(
                        method = HttpMethod.Get,
                        host = host.address,
                        port = host.port,
                        path = host.path
                    ) {
                        if(!pairingDone) {
                            val encryptedMessage = encryptPairingMessage(message= "startsync", password = pairingData.pairingMessage)
                            Log.d("Send PAIRINGDATA: ",  "${pairingData.deviceName} , $encryptedMessage")
                            val sendRequestRoutine = async { sendPairingRequest(PairingData(pairingData.deviceName, encryptedMessage)) }
                            sendRequestRoutine.await()
                            val receiveAck = async { receivePairAck() }
                            Log.d("KTORSYNC:", "waiting for pairing ACK")
                            val pairingOk = receiveAck.await()
                            if (pairingOk){
                                Log.d("KTORSYNC", "SERVER SAID OK")
                                _isPairingDone.value = pairingOk
                            }
                            pairingDone = pairingOk
                            //val pairingOk = receiveAck.await()
                        }

                        if (pairingDone){
                            Log.d("KTORSYNC: ", "CLIENT receiving notes!!!")
                            _isSyncingLive.value = true
                            val send = launch {
                                while (true) {
                                    ensureActive()
                                    if(ClientControl.SendUpdates.getAndSet(false)) {
                                        sendNotes(viewModel.notes.value ?: emptyList())
                                    }
                                    delay(25)
                                }
                            }
                            val receive = launch {
                                while (true) {
                                    ensureActive()
                                    receiveNotes()
                                    delay(25)
                                }
                            }

                            send.join()
                            receive.join()
                        }
                        Log.d("KTOR3:", "Closing connection")
                    }
                        //messageOutputRoutine.cancelAndJoin()
                } catch (e: ConnectException) {
                    Log.d("SyncClient:", "Failed to connect, aborting")
                } catch (e: Exception) {
                    Log.d("SyncClient:", "Error while connecting")
                } finally {
                    client.close()
                    _isSyncingLive.value = false
                    Log.d("ktor", "Connection closed. Goodbye!")
                }
            }
    }

    fun abortConnection(){
        client.close()
    }

    private fun encryptPairingMessage(message: String, password: String, ): String{
        val encryptedMessage = CryptUtil.encrypt(data = message, password = password)
        return encryptedMessage
    }

    private fun updateCachedNotes(notes: List<NoteProperty>){
        viewModel.clearAndUpdateCache(notes)
    }

    suspend fun DefaultClientWebSocketSession.sendPairingRequest(pairingData: PairingData)  {
        try {
            sendSerialized<PairingData>(pairingData)
        }
        catch (e: Exception) {
            if(e is CancellationException){
                throw e
            }
            println(e.localizedMessage)
        }
    }

    suspend fun DefaultClientWebSocketSession.receivePairAck(): Boolean {
        val responseBool =
        try {
            val resp = receiveDeserialized<PairingResponse>()
            resp
        } catch (e: WebsocketDeserializeException){
            Log.d("ktor client", "Failed to deserialize network data")
            PairingResponse(false, "")
        }
        catch(e: ClosedReceiveChannelException){
            Log.d("ktor client", "Network channel was closed")
            PairingResponse(false, "")
        }
        return responseBool.ok
    }

    suspend fun DefaultClientWebSocketSession.receiveNotes() {
        try {
            val networkNotes = receiveDeserialized<List<NoteProperty>>()
            updateCachedNotes(networkNotes)
        } catch (e: WebsocketDeserializeException){
            Log.d("ktor client", "Failed to deserialize network data")
            return
        }
        catch(e: ClosedReceiveChannelException){
            Log.d("ktor client", "Network channel was closed")
            return
        }
    }

    suspend fun DefaultClientWebSocketSession.sendNotes(notes: List<NoteProperty>) =
        withContext(Dispatchers.IO) {
            try {
                sendSerialized(notes)
                Log.d("KTORSYNC: ", "Sending notes to Server!!!")
            } catch (e: Exception) {
                if(e is CancellationException){
                    throw e
                }
                println(e.localizedMessage)
            }
        }

    suspend fun DefaultClientWebSocketSession.outputMessages() {
        try {
            for (message in incoming) {
                message as? Frame.Text ?: continue
                //updateMessages(message.readText())
            }
        } catch (e: Exception) {
            Log.d("ktor", "Error while receiving: " + e.localizedMessage)
        }
    }

}