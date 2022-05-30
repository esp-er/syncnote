package com.raywenderlich.jetnotes.data


import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.raywenderlich.jetnotes.data.network.HostData
import com.raywenderlich.jetnotes.domain.NoteProperty
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
import java.net.ConnectException

class SyncClient(private val cacheRepository: AndroidExternRepository, private val connectionInfo: HostData) {

    private val isConnectedState = MutableLiveData(false)

    val client = HttpClient {
        install(WebSockets){
            contentConverter = KotlinxWebsocketSerializationConverter(Json)
        }
    }
    //private var noteList: List<NoteProperty> = listOf<NoteProperty>()

    fun isSocketConnected() : LiveData<Boolean> = isConnectedState

    suspend fun connect(){
        try {
            client.webSocket(
                method = HttpMethod.Get,
                host = connectionInfo.address,
                port = connectionInfo.port,
                path = connectionInfo.path) {

                val receiveNotesRoutine = launch { receiveNotes() }
                isConnectedState.postValue(true)

                receiveNotesRoutine.join() // Wait for completion; either "exit" or error
                //messageOutputRoutine.cancelAndJoin()
            }
        }
        catch(e: ConnectException){
            Log.d("SyncClient:", "Failed to connect, aborting")
        }
        catch(e: Exception){
            Log.d("SyncClient:", "Error while connecting")
            e.printStackTrace()
        }
        finally {
            client.close()
            isConnectedState.postValue(false)
            Log.d("ktor", "Connection closed. Goodbye!")
        }
    }

    private fun updateNotes(notes: List<NoteProperty>){
        cacheRepository.clearAndSaveAll(notes)
    }

    suspend fun DefaultClientWebSocketSession.receiveNotes(){
        try {
            while (true) {
                val networkNotes = receiveDeserialized<List<NoteProperty>>()
                updateNotes(networkNotes)
                delay(10)
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