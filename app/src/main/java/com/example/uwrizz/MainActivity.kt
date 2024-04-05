@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.uwrizz

// Import everything that's necessary
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


// Replace this with your application's package name
private const val USER_PREFERENCES_NAME = "com.example.uwrizz"

// Just after the package declaration
//val Context.dataStore by preferencesDataStore(name = USER_PREFERENCES_NAME)

// The dataStore by delegate
private val Context.dataStore by preferencesDataStore(name = USER_PREFERENCES_NAME)

//val loggedInUserId = "mwjnyI8mTRfT287LGP0Ac7PJnbt1"



class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val client = HttpClient(CIO) {
            install(ContentNegotiation) {
                json(Json {
                    prettyPrint = true
                    isLenient = true
                    coerceInputValues = true // Coerce incorrect JSON values instead of failing
                    ignoreUnknownKeys = true // Ignore unknown keys in the JSON
                })
            }
            install(WebSockets) {
                // Configure WebSocket options if needed
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
                    ) // Replace with your desired credentials
                }
            }
            // Use the dataStore directly inside the MainScreen composable
            // Pass the client to the MainScreen composable
            MainScreen(client)
        }
    }
}


@Composable
fun MainScreen(client: HttpClient) {

    val user = FirebaseAuth.getInstance().currentUser
    user?.let {
        // Name, email address, and profile photo Url
        val name = user.displayName
        val email = user.email
        val photoUrl = user.photoUrl

        // Check if user's email is verified
        val emailVerified = user.isEmailVerified

        // The user's ID, unique to the Firebase project. Do NOT use this value to
        // authenticate with your backend server, if you have one. Use
        // FirebaseUser.getToken() instead.
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
        var currentChatUserId by remember { mutableStateOf<String?>(null) }  // To keep track of the current user selected for chat
        // Define a callback for when a user is clicked in the UsersListScreen
        val onUserClicked: (String) -> Unit = { clickedUserId ->
            // This should log the clicked user's ID to confirm it's being set
            Log.d("MainScreen", "User clicked: $clickedUserId")

            // Set the current screen to chat and store the clicked user's ID
            currentChatUserId = clickedUserId
            currentScreen = Screen.Chat
        }

        val onBackFromChatClicked: () -> Unit = {
            currentChatUserId = null // Clear the selected user ID
            currentScreen = Screen.Chat // Go back to the users list
        }

        LaunchedEffect(isLoggedIn) {
            currentScreen = if (isLoggedIn) Screen.Profile else Screen.Login
        }

        if (!isLoggedIn) { // fix here after -----------------------------------------------------------------------------------------------
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
                        // Logic when account is created, might be similar to onLoginSuccess
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

                // Perform any additional logout actions here, if needed

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

//                        Screen.Likes -> LikesScreen(exampleProfiles)
                        Screen.Profile -> ProfileSettingsScreen(
                            profileImage = ImageVector.vectorResource(R.drawable.ic_head), // Replace with your actual default image resource
                            onImageClick = {
                                // Define what happens when the "add image" button is clicked
                                // For example, opening a gallery or a photo picker
                            },
                            onNavigateToPreferences = {
                                // Define what happens when "Edit Preferences" button is clicked
                                // E.g., updating the state to navigate to the preferences screen
                                currentScreen = Screen.Preferences
                            },
                            onImageSelected = { uri ->
                                // Here you can handle the selected image URI.
                                // For example, updating the UI state or uploading the image to a server.
                            },
                            onNavigateToSurvey = {
                                currentScreen = Screen.Survey
                            },
                            context = context
                        )

                        Screen.Preferences -> PreferencesScreen(
                            onNavigateToProfile = {
                                // Define what happens when "Edit Preferences" button is clicked
                                // E.g., updating the state to navigate to the preferences screen
                                currentScreen = Screen.Profile
                            }
                        )

                        Screen.Survey -> SurveyScreen(
                            onNavigateToProfile = {
                                // Define what happens when "Edit Preferences" button is clicked
                                // E.g., updating the state to navigate to the preferences screen
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
        val selectedColor = Color.White // Change to your desired color for selected items

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
                    "Home",
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
                    tint = lightGrey // Adjust the color as needed
                )
            },
            label = {
                Text(
                    "Logout",
                    color = lightGrey // Adjust the color as needed
                )
            },
            selected = false, // Since it's not selectable
            onClick = { onLogoutClicked() }
        )
    }
}



fun Icon(chat: Screen, contentDescription: String) {


}

enum class Screen {
    Home, Chat, Profile, Preferences, Survey, Login, CreateAccount
}//removed Likes for now


