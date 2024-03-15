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

@Composable
fun ChatScreen() {
    var textState by remember { mutableStateOf(TextFieldValue("")) }
    val messages = remember { mutableStateListOf<String>() }
    val listState = rememberLazyListState()

    // Listen for changes in textState and scroll to the bottom when the keyboard is opened
    LaunchedEffect(textState) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Scaffold(
        bottomBar = {
            InputBar(
                textState = textState,
                onTextChanged = { newText -> textState = newText },
                onSend = {
                    if (textState.text.isNotBlank()) {
                        messages.add(textState.text)
                        textState = TextFieldValue("") // Clear the input field after sending a message
                        // No need to manually scroll here since the LaunchedEffect will handle it
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

@Composable
fun InputBar(
    textState: TextFieldValue,
    onTextChanged: (TextFieldValue) -> Unit,
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