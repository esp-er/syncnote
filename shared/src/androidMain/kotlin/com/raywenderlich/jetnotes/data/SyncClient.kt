package com.raywenderlich.jetnotes.data


import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.raywenderlich.jetnotes.MainViewModel
import com.raywenderlich.jetnotes.data.network.HostData
import com.raywenderlich.jetnotes.domain.NoteProperty
import com.raywenderlich.jetnotes.domain.PairingData
import com.raywenderlich.jetnotes.domain.PairingResponse
import com.raywenderlich.jetnotes.domain.random8Id
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.http.*
import io.ktor.serialization.*
import io.ktor.serialization.kotlinx.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.websocket.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.serialization.json.Json
import org.koin.androidx.viewmodel.dsl.*
import org.koin.androidx.viewmodel.scope.*
import java.net.ConnectException


class SyncClient(val viewModel: MainViewModel, var host: HostData,
                 var pairingDone: Boolean = false,
                 var pairingData: PairingData = PairingData("xiaomi note 10", "")
                 ) {
    //TODO: this class is not a view so shouldnt hold viewmodel or depend on it

    private val _isSyncingLive = MutableLiveData(false)
    val isSyncingLive: LiveData<Boolean> = _isSyncingLive
    private val _isPairingDone = MutableLiveData(pairingDone)
    val isPairingDone: LiveData<Boolean> = _isPairingDone

    val client = HttpClient {
        install(WebSockets){
            contentConverter = KotlinxWebsocketSerializationConverter(Json)
        }
    }
    //private var noteList: List<NoteProperty> = listOf<NoteProperty>()

    suspend fun connect(){
        if(host.address != "0.0.0.0"){
                try {
                    client.webSocket(
                        method = HttpMethod.Get,
                        host = host.address,
                        port = host.port,
                        path = host.path
                    ) {
                        if(!pairingDone) {
                            Log.d("Send PAIRINGDATA: ",  "${pairingData.deviceName} , ${pairingData.pairingCode}")
                            val sendRequestRoutine = async { sendPairingRequest(pairingData) }
                            sendRequestRoutine.await()
                            val receiveAck = async { receivePairAck() }
                            Log.d("KTORSYNC:", "waiting for pairing ACK")
                            val pairingOk = receiveAck.await()
                            if (pairingOk)
                                Log.d("KTORSYNC", "SERVER SAID OK")
                            _isPairingDone.postValue(pairingOk)
                            pairingDone = pairingOk
                            //val pairingOk = receiveAck.await()
                        }
                        if (pairingDone){
                            Log.d("KTORSYNC: ", "CLIENT receiving notes!!!")
                            _isSyncingLive.postValue(true)
                            val receiveNotesRoutine = launch { receiveNotes() }
                            receiveNotesRoutine.join() // Wait for completion; either "exit" or error
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
                    _isSyncingLive.postValue(false)
                    Log.d("ktor", "Connection closed. Goodbye!")
                }
            }
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
            PairingResponse(false)
        }
        catch(e: ClosedReceiveChannelException){
            Log.d("ktor client", "Network channel was closed")
            PairingResponse(false)
        }
        return responseBool.ok
    }

    suspend fun DefaultClientWebSocketSession.receiveNotes() {
        try {
            while (true) {
                val networkNotes = receiveDeserialized<List<NoteProperty>>()
                updateCachedNotes(networkNotes)
                delay(25)
            }
        } catch (e: WebsocketDeserializeException){
            Log.d("ktor client", "Failed to deserialize network data")
            return
        }
        catch(e: ClosedReceiveChannelException){
            Log.d("ktor client", "Network channel was closed")
            return
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

    suspend fun DefaultClientWebSocketSession.inputMessages() {
        while (true) {
            delay(1000)
            /*val message = readLine() ?: ""
            if (message.equals("exit", true)) return
            try {
                send(message)
            } catch (e: Exception) {
                println("Error while sending: " + e.localizedMessage)
                return
            }*/
        }
    }
}