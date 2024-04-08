@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.uwrizz

import UserDatabaseHelper
import PreferencesScreen
import SurveyScreen
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.map
import androidx.navigation.compose.rememberNavController
import com.example.uwrizz.ui.theme.UWRizzTheme
import com.google.firebase.auth.FirebaseAuth
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.websocket.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

private const val USER_PREFERENCES_NAME = "com.example.uwrizz"


private val Context.dataStore by preferencesDataStore(name = USER_PREFERENCES_NAME)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val client = HttpClient(CIO) {
            install(ContentNegotiation) {
                json(Json {
                    prettyPrint = true
                    isLenient = true
                    coerceInputValues = true
                    ignoreUnknownKeys = true
                })
            }
            install(WebSockets) {

            }
        }
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
                    dbHelper.addUser(
                        "admin",
                        "password"
                    )
                }
            }
            MainScreen(client)
        }
    }
}


@Composable
fun MainScreen(client: HttpClient) {

    val user = FirebaseAuth.getInstance().currentUser
    user?.let {
        val name = user.displayName
        val email = user.email
        val photoUrl = user.photoUrl

        // Check if user's email is verified
        val emailVerified = user.isEmailVerified

        // The user's ID, unique to the Firebase project.
        val uid = user.uid
    }

    UWRizzTheme {
        val scope = rememberCoroutineScope()
        val context = LocalContext.current
        val dataStore = context.dataStore
        val navController = rememberNavController()

        // Use collectAsState to observe the isLoggedIn state
        val isLoggedIn = dataStore.data
            .map { preferences ->
                // Here, we are defaulting to 'false' which means the user is not logged in
                preferences[booleanPreferencesKey("IS_LOGGED_IN")] ?: false
            }
            .collectAsState(initial = false).value
        Log.e("checking here :", "" + isLoggedIn)

        var currentScreen by remember { mutableStateOf(Screen.Login) }
        // To keep track of the current user selected for chat
        var currentChatUserId by remember { mutableStateOf<String?>(null) }
        // Define a callback for when a user is clicked in the UsersListScreen
        val onUserClicked: (String) -> Unit = { clickedUserId ->
            // This should log the clicked user's ID to confirm it's being set
            Log.d("MainScreen", "User clicked: $clickedUserId")

            // Set the current screen to chat and store the clicked user's ID
            currentChatUserId = clickedUserId
            currentScreen = Screen.Chat
        }

        val onBackFromChatClicked: () -> Unit = {
            // Clear the selected user ID
            currentChatUserId = null
            // Go back to the users list
            currentScreen = Screen.Chat
        }

        LaunchedEffect(isLoggedIn) {
            currentScreen = if (isLoggedIn) Screen.Profile else Screen.Login
        }

        if (!isLoggedIn) {
            when (currentScreen) {
                Screen.Login -> LoginScreen(
                    context = context,
                    onLoginSuccess = {
                        // Here we perform the login logic and upon success, we update the dataStore
                        scope.launch {
                            dataStore.edit { settings ->
                                settings[booleanPreferencesKey("IS_LOGGED_IN")] = true
                            }
                            Log.d("Login", "Current screen before update: $currentScreen")
                            currentScreen = Screen.Home
                            Log.d("Login", "Current screen after update: $currentScreen")
                        }
                    },
                    onNavigateToCreateAccount = {
                        // Update the current screen to navigate to Create Account screen
                        currentScreen = Screen.CreateAccount
                    },
                    )

                Screen.CreateAccount -> CreateAccount(
                    context = context,
                    onLoginSuccess = {
                        // Logic when account is created
                    },
                    onNavigateBack = {
                        // Navigate back to the login screen
                        currentScreen = Screen.Login
                    }
                )
                else -> Unit
            }
        } else {
            val onLogoutClicked: () -> Unit = {
                // Sign out the user from Firebase Authentication
                FirebaseAuth.getInstance().signOut()

                // Update the data store to reflect that the user is logged out
                scope.launch {
                    dataStore.edit { settings ->
                        settings[booleanPreferencesKey("IS_LOGGED_IN")] = false
                    }
                }
            }
            Scaffold(
                bottomBar = {
                    BottomNavigationBar(
                        currentScreen,
                        onNavigationItemSelected = { screen -> currentScreen = screen },
                                onLogoutClicked = onLogoutClicked)
                }
            ) { innerPadding ->
                Box(modifier = Modifier.padding(innerPadding)) {
                    when (currentScreen) {

                        Screen.Home -> MainContent()
                        Screen.Chat -> {
                            if (currentChatUserId != null) {
                                // If a user has been clicked, show the ChatScreen with the selected user ID
                                ChatScreen(
                                    currentUserId = user?.uid.orEmpty(),
                                    matchedUserId = currentChatUserId.orEmpty(),
                                    onBackClicked = onBackFromChatClicked // Pass the lambda here
                                )
                            } else {
                                // Otherwise, show the UsersListScreen
                                UsersListScreen(loggedInUserId = user?.uid.orEmpty(), onUserClicked = onUserClicked)
                            }
                        }
                        // Likes Screen has been removed, and considered redundant.
//                        Screen.Likes -> LikesScreen(exampleProfiles)
                        Screen.Profile -> ProfileSettingsScreen(
                            profileImage = ImageVector.vectorResource(R.drawable.ic_head), // Replace with your actual default image resource
                            onImageClick = {
                                // Define what happens when the "add image" button is clicked
                            },
                            onNavigateToPreferences = {
                                currentScreen = Screen.Preferences
                            },
                            onImageSelected = { uri ->
                                // Here handle the selected image URI.
                            },
                            onNavigateToSurvey = {
                                currentScreen = Screen.Survey
                            },
                            context = context
                        )

                        Screen.Preferences -> PreferencesScreen(
                            onNavigateToProfile = {
                                currentScreen = Screen.Profile
                            }
                        )

                        Screen.Survey -> SurveyScreen(
                            onNavigateToProfile = {
                                currentScreen = Screen.Profile
                            }
                        )
                        else -> Unit
                    }
                }
            }
        }
    }
}


@Composable
fun BottomNavigationBar(
    currentScreen: Screen,
    onNavigationItemSelected: (Screen) -> Unit,
    onLogoutClicked: () -> Unit
) {
    BottomNavigation(
        backgroundColor = Color(0xFF808080)
    ) {
        val lightGrey = Color(0xFFA8A8A8)
        val selectedColor = Color.White

        BottomNavigationItem(
            icon = {
                Icon(
                    Icons.Filled.Home,
                    contentDescription = "Home",
                    tint = if (currentScreen == Screen.Home) selectedColor else lightGrey
                )
            },
            label = {
                Text(
                    "Feed",
                    color = if (currentScreen == Screen.Home) selectedColor else lightGrey
                )
            },
            selected = currentScreen == Screen.Home,
            onClick = { onNavigationItemSelected(Screen.Home) }
        )
        BottomNavigationItem(
            icon = {
                Icon(
                    ImageVector.vectorResource(id = R.drawable.ic_chat),
                    contentDescription = "Chat",
                    tint = if (currentScreen == Screen.Chat) selectedColor else lightGrey
                )
            },
            label = {
                Text(
                    "Chat",
                    color = if (currentScreen == Screen.Chat) selectedColor else lightGrey
                )
            },
            selected = currentScreen == Screen.Chat,
            onClick = { onNavigationItemSelected(Screen.Chat) }
        )
        // removed likes from bottom menu
//        BottomNavigationItem(
//            icon = {
//                Icon(
//                    ImageVector.vectorResource(id = R.drawable.ic_add),
//                    contentDescription = "Likes",
//                    tint = if (currentScreen == Screen.Likes) selectedColor else lightGrey
//                )
//            },
//            label = {
//                Text(
//                    "Likes",
//                    color = if (currentScreen == Screen.Likes) selectedColor else lightGrey
//                )
//            },
//            selected = currentScreen == Screen.Likes,
//            onClick = { onNavigationItemSelected(Screen.Likes) }
//        )
        BottomNavigationItem(
            icon = {
                Icon(
                    ImageVector.vectorResource(id = R.drawable.ic_head),
                    contentDescription = "Profile",
                    tint = if (currentScreen == Screen.Profile) selectedColor else lightGrey
                )
            },
            label = {
                Text(
                    "Profile",
                    color = if (currentScreen == Screen.Profile) selectedColor else lightGrey
                )
            },
            selected = currentScreen == Screen.Profile,
            onClick = { onNavigationItemSelected(Screen.Profile) }
        )
        BottomNavigationItem(
            icon = {
                Icon(
                    Icons.Filled.Logout,
                    contentDescription = "Logout",
                    tint = lightGrey
                )
            },
            label = {
                Text(
                    "Logout",
                    color = lightGrey
                )
            },
            selected = false,
            onClick = { onLogoutClicked() }
        )
    }
}

enum class Screen {
    Home, Chat, Profile, Preferences, Survey, Login, CreateAccount
}//removed Likes for now


