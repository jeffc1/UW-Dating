@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.uwrizz

// Import everything that's necessary
import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.uwrizz.ui.theme.UWRizzTheme
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem

// ... (Rest of your imports, if any)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            UWRizzTheme {
                MainScreen()
            }
        }
    }
}
@Composable
fun MainScreen() {
    var currentScreen by remember { mutableStateOf(Screen.Home) }
    Scaffold(
        bottomBar = { BottomNavigationBar(currentScreen, onNavigationItemSelected = { screen -> currentScreen = screen }) }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (currentScreen) {
                Screen.Home -> MainContent()
                Screen.Chat -> ChatScreen()
                Screen.Preferences -> PreferencesScreen { /* Handle Preference Screen navigation */ }
            }
        }
    }
}

@Composable
fun BottomNavigationBar(currentScreen: Screen, onNavigationItemSelected: (Screen) -> Unit) {
    BottomNavigation {
        BottomNavigationItem(
            icon = { Icon(Icons.Filled.Home, contentDescription = "Home") },
            label = { Text("Home") },
            selected = currentScreen == Screen.Home,
            onClick = { onNavigationItemSelected(Screen.Home) }
        )
        BottomNavigationItem(
            icon = { Icon(Screen.Chat, contentDescription = "Chat") },
            label = { Text("Chat") },
            selected = currentScreen == Screen.Chat,
            onClick = { onNavigationItemSelected(Screen.Chat) }
        )
        BottomNavigationItem(
            icon = { Icon(Icons.Default.Settings, contentDescription = "Preferences") },
            label = { Text("Preferences") },
            selected = currentScreen == Screen.Preferences,
            onClick = { onNavigationItemSelected(Screen.Preferences) }
        )
    }
}

fun Icon(chat: Screen, contentDescription: String) {


}

enum class Screen {
    Home, Chat, Preferences
}
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



// THIS IS JEFFS STUFF


@Composable
fun MainContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Your main content goes here

        // Add a spacer to push the buttons to the bottom
        Spacer(modifier = Modifier.weight(1f))

        // Row of 4 buttons at the bottom
        Box(
            modifier = Modifier
                .fillMaxSize()
                .weight(12f)
        ) {
            ScrollableCard()
            XButton()
        }
        Spacer(modifier = Modifier.weight(1f))

        BottomButtonRow()
    }
}

@Composable
fun XButton() {
    FloatingActionButton(
        onClick = { },
        modifier = Modifier
            .padding(16.dp)
    ) {
        Icon(Icons.Filled.Close, "Floating action button.")
    }
}

@Composable
fun BottomButtonRow(modifier: Modifier = Modifier) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Add your buttons here
        Button(onClick = { /* Handle button click */ },
            modifier = Modifier.weight(1f)
        ) {
            Text("B1")
        }
        Button(onClick = { /* Handle button click */ },
            modifier = Modifier.weight(1f)
        ) {
            Text("B2")
        }
        Button(onClick = { /* Handle button click */ },
            modifier = Modifier.weight(1f)
        ) {
            Text("B3")
        }
        Button(onClick = { /* Handle button click */ },
            modifier = Modifier.weight(1f)
        ) {
            Text("B4")
        }
    }
}

@Composable
fun ScrollableCard() {
    LazyColumn(
        modifier = Modifier
            .padding(bottom = 5.dp)
    ) {
        // Larger square card at the bottom for a photo
        item {
            LargeSquareCard()
            LargeSquareCard()
            LargeSquareCard()
            LargeSquareCard()
            LargeSquareCard()
            LargeSquareCard()
            LargeSquareCard()
            LargeSquareCard()
            LargeSquareCard()
            LargeSquareCard()

        }
    }
}

@Composable
fun LargeSquareCard() {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp)
    ) {
        Text(
            text = "Testing",
            modifier = Modifier
                .padding(30.dp),
            textAlign = TextAlign.Center,
        )
    }
}

@Preview(showBackground = true)
@Composable
fun MainContentPreview() {
    UWRizzTheme {
        MainContent()
    }
}

// ALVINS STUFF

//@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun PreferencesScreen(onNavigate: (PreferenceType) -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Preferences", color = Color.White) },
//                backgroundColor = Color(0xFF000000) // Black
            )
        },
        content = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 8.dp)
            ) {
                PreferenceItem(title = "Gender", summary = "Man", onClick = { onNavigate(PreferenceType.Gender) })
                PreferenceItem(title = "I'm interested in", summary = "Women", onClick = { onNavigate(PreferenceType.Interest) })
                PreferenceItem(title = "Program", summary = "Not Engineering", onClick = { onNavigate(PreferenceType.Neighborhood) })
                PreferenceItem(title = "Age range", summary = "18 - 23", onClick = { onNavigate(PreferenceType.AgeRange) })
                PreferenceItem(title = "Height", summary = "3'0\" - 7'0\"", onClick = { onNavigate(PreferenceType.Height) })
            }
        }
    )
}

@Composable
fun PreferenceItem(title: String, summary: String, onClick: () -> Unit) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .clickable(onClick = onClick)
        .padding(16.dp)) {
        Text(title, fontWeight = FontWeight.Bold)
        Text(summary, color = Color.Gray)
        Divider(color = Color.LightGray, thickness = 1.dp, modifier = Modifier.padding(top = 8.dp))
    }
}

enum class PreferenceType {
    Gender, Interest, Neighborhood, AgeRange, Distance, Height, Ethnicity, Religion
}


@Preview(showBackground = true)
@Composable
fun PreferencesScreenPreview() {
    PreferencesScreen(onNavigate = {})
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    UWRizzTheme {
        MainScreen()
    }
}