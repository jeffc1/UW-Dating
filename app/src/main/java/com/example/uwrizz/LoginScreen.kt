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
import androidx.compose.ui.res.painterResource
import androidx.compose.runtime.Composable
import androidx.compose.foundation.Image
import androidx.compose.foundation.text.ClickableText
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.uwrizz.ui.theme.Red
import com.example.uwrizz.ui.theme.Typography
import com.example.uwrizz.ui.theme.interFamily


@Composable
fun GeeseApprovedDating() {
    Row(modifier = Modifier.padding(bottom = 5.dp), horizontalArrangement = Arrangement.spacedBy((-8).dp)) {
        Text(
            text = "Geese Approved ",
            style = TextStyle(
                fontFamily = interFamily,
                fontWeight = FontWeight.Bold,
                letterSpacing = -2.sp,
            ),
            color = Color.Black,
            fontSize = 32.sp,
            modifier = Modifier.padding(end = 8.dp)
        )
        Text(
            text = "Dating.",
            style = TextStyle(
                fontFamily = interFamily,
                fontWeight = FontWeight.Bold,
                letterSpacing = -2.sp,
            ),
            color = Red,
            fontSize = 32.sp
        )
    }
}

@Composable
fun LoginScreen(
    context: Context,
    onLoginSuccess: () -> Unit,
    onNavigateToCreateAccount: () -> Unit
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val userDatabaseHelper = remember { UserDatabaseHelper(context) }
    val logo = painterResource(R.drawable.uwrizzlogo)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(32.dp))
        Image(
            painter = logo,
            contentDescription = "Uwrizz Logo",
            modifier = Modifier
                .size(200.dp)
                .align(Alignment.CenterHorizontally)
        )
        GeeseApprovedDating()
        Text(
            text = "uWaterloo's very own dating app.",
            style = TextStyle(
                fontFamily = interFamily,
                fontWeight = FontWeight.Normal,
                letterSpacing = 0.sp,
            ),
            color = Color.Black,
            fontSize = 16.sp
        )
        Spacer(modifier = Modifier.weight(1f))
        // ------------------
        // username password.
        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(10.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        ClickableText(
            text = AnnotatedString("Forgot password?"),
            onClick = {

            }
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
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
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Log in",
                fontSize = 20.sp
            )
        }
        Spacer(modifier = Modifier.weight(2f))
        Text(
            text = "Don't have an account? "
        )
        ClickableText(
            text = AnnotatedString("Sign Up", spanStyle = SpanStyle(color = Color.Red)),
            onClick = {
                onNavigateToCreateAccount()
            }
        )
    }
}


