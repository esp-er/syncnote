package com.raywenderlich.android.jetnotes.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.*
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewModelScope
import com.raywenderlich.android.jetnotes.domain.QRAnalyzer
import com.raywenderlich.jetnotes.data.SyncClient
import com.raywenderlich.jetnotes.data.network.HostData
import com.raywenderlich.jetnotes.domain.PairingData
import com.raywenderlich.jetnotes.routing.NotesRouter
import com.raywenderlich.jetnotes.routing.Screen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun PairDeviceUI(onConnect: (HostData, String) -> Unit) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.align(Alignment.TopCenter).fillMaxWidth(0.9f)){

            var ipString by remember { mutableStateOf("192.168.0.149") }
            var portStr by remember { mutableStateOf("9000") }
            var sharedCode by remember { mutableStateOf("") }

            fun Modifier.hcenter() = Modifier.align(Alignment.CenterHorizontally)
            Row(modifier = Modifier.padding(horizontal = 64.dp, vertical = 12.dp).hcenter()){
                Text("Start the SyncNote desktop app and navigate to the Phone Notes screen.\nBoth devices must be on the same WiFi/LAN.\nPress Button to start scan:")

            }
            Button(onClick = { NotesRouter.navigateTo(Screen.Pairing)}, modifier = Modifier.hcenter().padding(top = 24.dp)) {
                Column {
                    Text("Scan QR code")
                    Icon(
                        imageVector = Icons.Default.QrCode, "QR icon",
                        modifier = Modifier.align(Alignment.CenterHorizontally).padding(vertical = 4.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(48.dp))
            Row(modifier = Modifier.padding(top = 48.dp).hcenter()){
                Text("Or, Enter the desktop pairing information manually:", modifier = Modifier.hcenter())
            }
            Spacer(modifier = Modifier.height(8.dp))
            val codeMaxChars = 8
            TextField(
                value = sharedCode,
                onValueChange = { if(it.length <= codeMaxChars && isLettersOnly(it)) sharedCode = it.uppercase()},
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .fillMaxWidth()
                    .padding(horizontal = 40.dp),
                singleLine = true,
                placeholder = {
                    Row {
                        Spacer(Modifier.fillMaxWidth(0.25f))
                        Text(
                            "Pairing Code (8 Letters)",
                            textAlign = TextAlign.Center,
                            fontSize = 16.sp
                        )
                        Spacer(Modifier.fillMaxWidth(0.25f))
                    } },
                textStyle = LocalTextStyle.current.copy(fontSize = 20.sp, textAlign = TextAlign.Center),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
            )
            Row(
                modifier = Modifier
                    .hcenter()
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp, vertical = 6.dp)
            ) {
                TextField(
                    value = ipString,
                    onValueChange = { ipString = it },
                    modifier = Modifier
                        .weight(2.5f)
                        .padding(horizontal = 2.dp),
                    label = { Text("Network Address") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                val portMaxChars = 5
                TextField(
                    value = portStr,
                    onValueChange = { if(it.length <= portMaxChars) portStr = it},
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 1.5.dp),
                    placeholder = { Text("(8 Letters)")},
                    label = { Text("Port") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

            }
            Row(modifier = Modifier.hcenter()){
                Button(
                    onClick = {
                        onConnect(
                            HostData(ipString, portStr.toInt(), "/syncnote"),
                            "ASDFQWER"
                        )
                    }
                ){
                    Text("Connect")
                }
            }
        }
    }
}
fun isLettersOnly(string: String): Boolean {
    return string.filter { it in 'A'..'Z' || it in 'a'..'z' }.length == string.length
}