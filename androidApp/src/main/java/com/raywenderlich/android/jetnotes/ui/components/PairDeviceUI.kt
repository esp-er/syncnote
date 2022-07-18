package com.raywenderlich.android.jetnotes.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.TextField
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
import androidx.compose.ui.text.input.KeyboardType
import com.raywenderlich.android.jetnotes.domain.QRAnalyzer
import com.raywenderlich.jetnotes.routing.NotesRouter
import com.raywenderlich.jetnotes.routing.Screen

@Composable
fun PairDeviceUI() {

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.align(Alignment.TopCenter).fillMaxWidth(0.9f)){
            fun Modifier.hcenter() = Modifier.align(Alignment.CenterHorizontally)
            Row(modifier = Modifier.padding(horizontal = 64.dp, vertical = 12.dp).hcenter()){
                Text("Start the SyncNote desktop app and navigate to the Phone Notes screen.\nBoth devices must be on the same WiFi/LAN.")
            }
            Button(onClick = { NotesRouter.navigateTo(Screen.Pairing)}, modifier = Modifier.hcenter().padding(vertical = 6.dp)) {
                Column {
                    Text("Scan QR code")
                    Icon(
                        imageVector = Icons.Default.QrCode, "QR icon",
                        modifier = Modifier.align(Alignment.CenterHorizontally).padding(vertical = 4.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.padding(top = 12.dp).hcenter()){
                Text("Or, Enter the desktop app host information manually:", modifier = Modifier.hcenter())
            }
            Row(
                modifier = Modifier
                    .hcenter()
                    .fillMaxWidth()
                    .padding(horizontal = 64.dp, vertical = 6.dp)
            ) {
                var ipString by remember { mutableStateOf("0.0.0.0") }
                var portStr by remember { mutableStateOf("9000") }
                TextField(
                    value = ipString,
                    onValueChange = { ipString = it },
                    modifier = Modifier
                        .weight(2.5f)
                        .padding(horizontal = 2.dp),
                    label = { Text("Net Address") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                TextField(
                    value = portStr,
                    onValueChange = { portStr = it },
                    modifier = Modifier
                        .weight(1.5f)
                        .padding(horizontal = 2.dp),
                    label = { Text("Port") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }
        }
    }
}
