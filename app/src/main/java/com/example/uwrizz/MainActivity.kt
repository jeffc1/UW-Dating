@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.uwrizz

// Import everything that's necessary
import UserDatabaseHelper
import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.booleanPreferencesKey

import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import androidx.datastore.preferences.core.Preferences

import com.example.uwrizz.ui.theme.UWRizzTheme
import kotlinx.coroutines.launch
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
//import androidx.compose.runtime.flow.collectAsState


import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.Dispatchers


// Replace this with your application's package name
private const val USER_PREFERENCES_NAME = "com.example.uwrizz"

// Just after the package declaration
//val Context.dataStore by preferencesDataStore(name = USER_PREFERENCES_NAME)

// The dataStore by delegate
private val Context.dataStore by preferencesDataStore(name = USER_PREFERENCES_NAME)


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize the login state to logged out
        val dataStore = applicationContext.dataStore
        CoroutineScope(Dispatchers.IO).launch {
            dataStore.edit { settings ->
                settings[booleanPreferencesKey("IS_LOGGED_IN")] = false
            }
        }

        setContent {
            fun isFirstRun(): Boolean {
                val prefs = getSharedPreferences("prefs", Context.MODE_PRIVATE)
                val isFirstRun = prefs.getBoolean("isFirstRun", true)
                if (isFirstRun) {
                    with(prefs.edit()) {
                        putBoolean("isFirstRun", false)
                        apply()
                    }
                }
                return isFirstRun
            }

            UWRizzTheme {
                // State to manage whether the user is logged in or not
                val scope = rememberCoroutineScope()
                val dataStore = applicationContext.dataStore
                if (isFirstRun()) {
                    val dbHelper = UserDatabaseHelper(this@MainActivity)
                    dbHelper.addUser("admin", "password") // Replace with your desired credentials
                }

                // Use the dataStore directly inside the MainScreen composable
                MainScreen()
            }
        }
    }
}

@Composable
fun LoginScreen(context: Context, onLoginSuccess: () -> Unit) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val userDatabaseHelper = remember { UserDatabaseHelper(context) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        TextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            Log.d("LoginScreen", "Button clicked with username: $username and password: $password")
            // Here you would validate the username and password
            val isValidUser = userDatabaseHelper.validateUser(username, password)
            if (isValidUser) {
                Log.d("LoginScreen", "Login successful")
                onLoginSuccess()
            } else {
                Log.d("LoginScreen", "Login failed")
                // Optionally, show an error message to the user
            }
        }) {
            Text("Log in")
        }
    }
}


@Composable
fun MainScreen() {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val dataStore = context.dataStore

    // Use collectAsState to observe the isLoggedIn state
    val isLoggedIn = dataStore.data
        .map { preferences ->
            // Here, we are defaulting to 'false' which means the user is not logged in
            preferences[booleanPreferencesKey("IS_LOGGED_IN")] ?: false
        }
        .collectAsState(initial = false).value
Log.e("checking here :", ""+isLoggedIn)
    if (isLoggedIn) {
        var currentScreen by remember { mutableStateOf(Screen.Home) }
        Scaffold(
            bottomBar = { BottomNavigationBar(currentScreen, onNavigationItemSelected = { screen -> currentScreen = screen }) }
        ) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                when (currentScreen) {
                    Screen.Home -> MainContent()
                    Screen.Chat -> ChatScreen()
                    Screen.Likes -> LikesScreen { /* Handle likes screen */ }
                    Screen.Preferences -> PreferencesScreen { /* Handle Preference Screen navigation */ }
                }
            }
        }

    } else {
        // The user is not logged in, show the login screen
        LoginScreen(context = context, onLoginSuccess = {
            // Here we perform the login logic and upon success, we update the dataStore
            scope.launch {
                dataStore.edit { settings ->
                    settings[booleanPreferencesKey("IS_LOGGED_IN")] = true
                }
            }
        })
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
        BottomNavigationItem(
            icon = { Icon(Icons.Default.Settings, contentDescription = "Likes") },
            label = { Text("Likes") },
            selected = currentScreen == Screen.Likes,
            onClick = { onNavigationItemSelected(Screen.Likes) }
        )
    }
}

fun Icon(chat: Screen, contentDescription: String) {


}

enum class Screen {
    Home, Chat, Preferences, Likes
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
fun ScrollableCard() {
    LazyColumn(
        modifier = Modifier
            .padding(bottom = 5.dp)
    ) {
        // Larger square card at the bottom for a photo
        item {
            InfoCard("Studying", "💻","CompEng")
            InfoCard("Fav Sport", "🎾", "Tennis :)")
            CustomCard("A typical sunday", "running 5 marathons in 5 countries")
            CustomCard("Green flags I look for", "loving Netflix!")
        }
    }
}


@Composable
fun CustomCard(title: String, content: String) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Title (left-justified, not bold)
            Text(
                text = title,
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(bottom = 8.dp),
                textAlign = TextAlign.Start,
                fontWeight = FontWeight.Normal, // Not bold
                fontSize = 10.sp
            )

            // Content (left-justified, bold, bigger)
            Text(
                text = content,
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(bottom = 16.dp),
                textAlign = TextAlign.Start,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp // Adjust the size as needed
            )

            // Heart emoji button (bottom right)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(top = 8.dp)
                    .align(Alignment.End)
            ) {
                Spacer(modifier = Modifier.weight(1f))
                IconButton(
                    onClick = {
                        // Add your onClick logic here for the heart button
                    }
                ) {
                    Text("❤️") // Heart emoji
                }
            }
        }
    }
}
@Composable
fun InfoCard(
    topText: String,
    emoji: String,
    bottomText: String
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
        modifier = Modifier
            .padding(5.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Text at the top, horizontally centered
            Text(
                text = topText,
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .align(Alignment.CenterHorizontally),
                textAlign = TextAlign.Center
            )

            // Big emoji in the middle, horizontally centered
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(16.dp)
                    .align(Alignment.CenterHorizontally)
            ) {
                Text(
                    text = emoji,
                    fontSize = 40.sp,
                    modifier = Modifier
                        .align(Alignment.Center)
                )
            }

            // Text at the bottom, horizontally centered
            Text(
                text = bottomText,
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .align(Alignment.CenterHorizontally),
                textAlign = TextAlign.Center
            )
        }
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
                PreferenceItem(
                    title = "I'm interested in",
                    summary = "Women",
                    onClick = { onNavigate(PreferenceType.Interest) })
                PreferenceItem(
                    title = "Program",
                    summary = "Not Engineering",
                    onClick = { onNavigate(PreferenceType.Neighborhood) })
                PreferenceItem(
                    title = "Age range",
                    summary = "18 - 23",
                    onClick = { onNavigate(PreferenceType.AgeRange) })
                PreferenceItem(
                    title = "Height",
                    summary = "3'0\" - 7'0\"",
                    onClick = { onNavigate(PreferenceType.Height) })
            }
        }
    )
}

@Composable
fun PreferenceItem(title: String, summary: String, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp)
    ) {
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

// RILEY'S STUFF 
@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun LikesScreen(onNavigate: (LikeType) -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Likes", color = Color.Black) },
//                backgroundColor = Color(0xFF000000) // Black
            )
        },
        content = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 8.dp)
            ) {
                LikeItem(title = "Books", summary = "Book1, Book2", onClick = { onNavigate(LikeType.Books) })
                LikeItem(
                    title = "Books",
                    summary = "Harry Potter and the Philosopher's Stone",
                    onClick = { onNavigate(LikeType.Books) })
                LikeItem(
                    title = "Movies",
                    summary = "Guardians of the Galaxy Vol 3",
                    onClick = { onNavigate(LikeType.Movies) })
                LikeItem(title = "Sports", summary = "Swimming, biking", onClick = { onNavigate(LikeType.Sports) })
                LikeItem(title = "Music", summary = "Taylor Swift", onClick = { onNavigate(LikeType.Music) })
                LikeItem(title = "Food", summary = "Sushi", onClick = { onNavigate(LikeType.Foods) })
            }
        }
    )
}

@Composable
fun LikeItem(title: String, summary: String, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp)
    ) {
        Text(title, fontWeight = FontWeight.Bold)
        Text(summary, color = Color.Gray)
    }
}

enum class LikeType {
    Books, Movies, Sports, Music, Foods
}

@Preview(showBackground = true)
@Composable
fun LikesScreenPreview() {
    LikesScreen(onNavigate = {})
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    UWRizzTheme {
        MainScreen()
    }
}

