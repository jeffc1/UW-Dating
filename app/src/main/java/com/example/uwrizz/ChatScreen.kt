package com.example.uwrizz

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.example.uwrizz.Message


import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.websocket.*
import io.ktor.util.*
import io.ktor.websocket.readText
import kotlinx.coroutines.*

import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

private var socket: WebSocketSession? = null
suspend fun DefaultClientWebSocketSession.outputMessages() {
    try {
        for (message in incoming) {
            message as? Frame.Text ?: continue
            println(message.readText())
        }
    } catch (e: Exception) {
        println("Error while receiving: " + e.localizedMessage)
    }
}

suspend fun DefaultClientWebSocketSession.inputMessages() {
    while (true) {
        val message = readLine() ?: ""
        if (message.equals("exit", true)) return
        try {
            send(message)
        } catch (e: Exception) {
            println("Error while sending: " + e.localizedMessage)
            return
        }
    }
}

suspend fun sendMessage(message: String) {
    try {
        socket?.send(Frame.Text(message))
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

@Composable
fun ChatScreen(client: HttpClient) {
    var textState by remember { mutableStateOf("") }
    val messages = remember { mutableStateListOf<String>() }
    val listState = rememberLazyListState()
    var sendMessageRequested by remember { mutableStateOf(false) }
    var currentMessage by remember { mutableStateOf("") }
    var yourIP = "184.147.53.88"

    // Connect to WebSocket when Composable enters the Composition
    LaunchedEffect(Unit) {

        try {
            val existingMessages: List<Message> = client.get("http://$yourIP:8082/messages").body()
            messages.addAll(existingMessages.map { it.text })
        } catch (e: Exception) {
            println("Error fetching messages: ${e.localizedMessage}")
            println("" +client.get("http://$yourIP:8082/messages").body())
        }



//        val response: HttpResponse = client.get("http://10.32.87.177:8082/messages")
//        if (response.status == HttpStatusCode.OK) {
//            val existingMessages = response.body<List<String>>() // Replace with the actual expected format
//            messages.addAll(existingMessages)
//        }

        client.webSocket(
            method = HttpMethod.Get,
            host = "184.147.53.88",
            port = 8082,
            path = "/chat-socket"
        ) {
            socket = this
            val currentUsername = "ReplaceWithActualUsername" // Replace with actual logic to obtain the current username
            val messageOutputRoutine = launch { outputMessages(messages, currentUsername) }
            messageOutputRoutine.join()
        }
    }

    LaunchedEffect(sendMessageRequested) {
        if (sendMessageRequested && currentMessage.isNotBlank()) {
            try {
                println("Attempting to send message: $currentMessage")
                if (socket?.isActive == true) {
                    socket?.send(Frame.Text(currentMessage))
                    println("Message sent: $currentMessage")
                } else {
                    println("WebSocket session is not active. Reconnecting...")
                    // Handle reconnection logic here
                }
            } catch (e: Exception) {
                println("Failed to send message: ${e.localizedMessage}")
            }
            sendMessageRequested = false // Reset the request flag
        }
    }

    Scaffold(
        bottomBar = {

            InputBar(
                textState = textState,
                onTextChanged = { newText -> textState = newText.toString() },
                onSend = {
                    if (textState.isNotBlank()) {
                        // messages.add(textState) // Remove this line
                        currentMessage = textState
                        sendMessageRequested = true // Trigger the LaunchedEffect
                        textState = "" // Clear the input field
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            items(messages) { message ->
                MessageCard(message)
            }
        }
    }
}

suspend fun DefaultClientWebSocketSession.outputMessages(
    messages: MutableList<String>,
    currentUser: String
) {
    try {
        for (frame in incoming) {
            frame as? Frame.Text ?: continue
            val receivedText = frame.readText()
            val message = Json.decodeFromString<Message>(receivedText)
            // Assuming you have a field in Message that stores the username
            if (message.username != currentUser) {
                val formattedMessage = "${message.username}: ${message.text}"
                messages.add(formattedMessage)
            }
        }
    } catch (e: Exception) {
        println("Error while receiving: " + e.localizedMessage)
    }
}


@Composable
fun InputBar(
    textState: String, // textState is a String
    onTextChanged: (String) -> Unit, // onTextChanged accepts and returns a String
    onSend: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextField(
            value = textState,
            onValueChange = onTextChanged,
            modifier = Modifier.weight(1f),
            placeholder = { Text("Enter a message") },
            singleLine = true
        )
        Button(
            onClick = onSend,
            modifier = Modifier.padding(start = 8.dp)
        ) {
            Text("Send")
        }
    }
}


@Composable
fun MessageCard(message: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Text(
            text = message,
            modifier = Modifier.padding(8.dp)
        )
    }
}