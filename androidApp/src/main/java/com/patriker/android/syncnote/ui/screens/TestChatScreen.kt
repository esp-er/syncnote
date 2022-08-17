package com.patriker.android.syncnote.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp

@Composable
private fun SendMessageField(
    modifier: Modifier = Modifier,
    label: String,
    onTextChange: (String) -> Unit
) {
    var text by remember { mutableStateOf(TextFieldValue(""))}

    TextField(
        value = text,
        onValueChange = { newText -> text = newText },
        label = { Text(label) },
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        colors = TextFieldDefaults.textFieldColors( //"default" Data structure used to define colors
            backgroundColor = MaterialTheme.colors.surface
        )
    )
}
@Composable
private fun MessageField(
    modifier: Modifier = Modifier,
    text: String,
    onTextChange: (String) -> Unit
) {
    TextField(
        value = text,
        onValueChange = onTextChange,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        readOnly = true,
        colors = TextFieldDefaults.textFieldColors( //"default" Data structure used to define colors
            backgroundColor = MaterialTheme.colors.surface
        )
    )
}
/*
@Composable
fun TestChatScreen(mainViewModel: MainViewModel){
    val inbox = mainViewModel.inboxTmp.observeAsState(listOf())

    Column(modifier = Modifier.fillMaxSize()) {
        MessageField(
            modifier = Modifier
                .weight(9f)
                .padding(top = 16.dp),
            text = if(inbox.value.isNotEmpty()) {
                inbox.value.joinToString("\n")
            } else "",
            onTextChange = {}
        )

        Row(modifier = Modifier
            .weight(1f)
            .padding(bottom = 20.dp)){
            SendMessageField(
                modifier = Modifier.weight(6f),
                label = "Message",
                onTextChange = { }
            )
            Button(
                onClick = {},
                modifier = Modifier
                    .weight(2f)
                    .heightIn(50.dp),
            ) {
                Text("Send")
            }
        }
    }

}*/