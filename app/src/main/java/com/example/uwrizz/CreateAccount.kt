package com.example.uwrizz

import android.content.Context
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uwrizz.ui.theme.interFamily
import UserDatabaseHelper
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

@Composable
fun CreateAccount(
    context: Context,
    onLoginSuccess: () -> Unit,
    onNavigateBack: () -> Unit
) {
    var firstname by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val logo = painterResource(R.drawable.uwrizzlogo)
    // Initialize Firebase Auth
    val auth = FirebaseAuth.getInstance()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {

        Spacer(modifier = Modifier.height(12.dp))
        Image(
            painter = logo,
            contentDescription = "Uwrizz Logo",
            modifier = Modifier
                .size(200.dp)
                .align(Alignment.CenterHorizontally)
        )
        Button(
            onClick = {
                onNavigateBack()
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp)
        ) {
            androidx.compose.material.Text("Back to Login")
        }
        Spacer(modifier = Modifier.height(40.dp))
        // ------------------
        // username password.
        OutlinedTextField(
            value = firstname,
            onValueChange = { firstname = it },
            label = { Text("First Name") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(20.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                if (!isValidEmail(email)) {
                    Toast.makeText(context, "Invalid email address.", Toast.LENGTH_SHORT).show()
                    return@Button
                }
                if (password.length < 6) {
                    Toast.makeText(context, "Password must be at least 6 characters long.", Toast.LENGTH_SHORT).show()
                    return@Button
                }
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val user = auth.currentUser
                            // Send email verification link
                            user?.sendEmailVerification()
                                ?.addOnCompleteListener { verificationTask ->
                                    if (verificationTask.isSuccessful) {
                                        // Email verification link sent successfully
                                        Toast.makeText(context, "Account Created Successfully. Verification email sent.",
                                            Toast.LENGTH_SHORT).show()
                                        Log.d("CreateAccount", "createUserWithEmail:success")
                                        onLoginSuccess()
                                    } else {
                                        // Failed to send verification email
                                        Log.e("CreateAccount", "Failed to send verification email.", verificationTask.exception)
                                        Toast.makeText(context, "Failed to send verification email.",
                                            Toast.LENGTH_SHORT).show()
                                        // Continue with the rest of the registration process or handle the error
                                    }
                                }
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("CreateAccount", "createUserWithEmail:failure", task.exception)
                            Toast.makeText(context, "Authentication failed.",
                                Toast.LENGTH_SHORT).show()
                        }
                    }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp)
        ) {
            androidx.compose.material.Text("Register Account")
        }
    }
}

private fun isValidEmail(email: String): Boolean {
    return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
}