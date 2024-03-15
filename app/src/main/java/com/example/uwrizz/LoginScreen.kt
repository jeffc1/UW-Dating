package com.example.uwrizz

import UserDatabaseHelper
import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp

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