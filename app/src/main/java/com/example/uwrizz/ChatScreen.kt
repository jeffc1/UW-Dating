package com.example.uwrizz

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(currentUserId: String, matchedUserId: String, onBackClicked: () -> Unit) {
    val firestore = FirebaseFirestore.getInstance()
    var textState by remember { mutableStateOf("") }
    val messages: SnapshotStateList<Message> = remember { mutableStateListOf() }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    // Create a MutableStateFlow to hold messages
    val messagesFlow = remember { MutableStateFlow<List<Message>>(emptyList()) }
    var matchedUser by remember { mutableStateOf<User?>(null) }



    // Fetch the matched user's details from Firestore
    LaunchedEffect(matchedUserId) {
        firestore.collection("users").document(matchedUserId).get()
            .addOnSuccessListener { documentSnapshot ->
                matchedUser = documentSnapshot.toObject<User>()
            }
            .addOnFailureListener { e ->
                Log.e("ChatScreen", "Error fetching matched user details", e)
            }
    }

    // Fetch messages from Firestore
    DisposableEffect(key1 = currentUserId, key2 = matchedUserId) {
        val chatId = generateChatId(currentUserId, matchedUserId)
        val chatQuery = firestore.collection("messages")
            .whereEqualTo("chatId", chatId)
            .orderBy("timestamp", Query.Direction.ASCENDING)



        val listenerRegistration = chatQuery.addSnapshotListener { snapshot, error ->
            if (error != null) {
                // Handle the error
                Log.e("ChatScreen", "Error getting messages", error)
                return@addSnapshotListener
            }
            snapshot?.documents?.mapNotNull { it.toObject<Message>() }?.also { newMessages ->
                messages.clear()
                messages.addAll(newMessages)
            }
        }

        onDispose {
            listenerRegistration.remove()
        }
    }


    // Collect the messages from StateFlow and update the UI when there's a change
    val collectedMessages = messagesFlow.asStateFlow().collectAsState()

    // UI for chat screen
    Scaffold(
        topBar = {
            // Add the TopAppBar with the back button
            SmallTopAppBar(
                title = { Text(matchedUser?.firstName ?: "Chat") },
                navigationIcon = {
                    IconButton(onClick = { onBackClicked() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = {
            InputBar(
                textState = textState,
                onTextChanged = { newText -> textState = newText }, // Assign the newText to textState
                onSend = {
                    if (textState.isNotBlank()) {
                        coroutineScope.launch {
                            sendMessage(
                                firestore = firestore,
                                currentUserId = currentUserId,
                                matchedUserId = matchedUserId,
                                messageText = textState
                            )
                            textState = "" // Clear the input field
                        }
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
                MessageCard(
                    message = message.text,
                    isCurrentUser = message.senderId == currentUserId
                )
            }
        }
    }
}

fun generateChatId(userId1: String, userId2: String): String {
    return listOf(userId1, userId2).sorted().joinToString("-")
}

// Function to send a message to Firestore
// Adjusted send message function, non-suspending
// Function to send a message to Firestore
fun sendMessage(firestore: FirebaseFirestore, messageText: String, currentUserId: String, matchedUserId: String) {
    if (currentUserId.isNotEmpty() && matchedUserId.isNotEmpty()) {
        val chatId = generateChatId(currentUserId, matchedUserId)
        // Construct new message object
        val newMessage = Message(
            senderId = currentUserId,
            receiverId = matchedUserId,
            text = messageText,
            timestamp = System.currentTimeMillis(), // Use server timestamp as needed
            chatId = chatId
        )

        // Add the new message to the Firestore collection
        firestore.collection("messages").add(newMessage)
            .addOnSuccessListener { documentReference ->
                Log.d("ChatScreen", "Message sent with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                Log.e("ChatScreen", "Failed to send message: $e")
            }
    } else {
        Log.e("ChatScreen", "Current user ID or matched user ID is empty")
    }
}




@Composable
fun InputBar(
    textState: String,
    onTextChanged: (String) -> Unit,
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
fun MessageCard(message: String, isCurrentUser: Boolean) {
    val bubbleColor = if (isCurrentUser) Color(0xFF007AFF) else Color.LightGray
    val textColor = if (isCurrentUser) Color.White else Color.Black
    val alignment = if (isCurrentUser) Alignment.CenterEnd else Alignment.CenterStart

    Box(
        contentAlignment = alignment,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = bubbleColor,
            modifier = Modifier
                .padding(2.dp)
        ) {
            Text(
                text = message,
                color = textColor,
                modifier = Modifier
                    .padding(8.dp)
                    .widthIn(max = 300.dp) // Max width for bubble
            )
        }
    }
}
