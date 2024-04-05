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
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.auth.FirebaseAuthException

@Composable
fun CreateAccount(
    context: Context,
    onLoginSuccess: () -> Unit,
    onNavigateBack: () -> Unit
) {
    var firstname by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var reenterPassword by remember { mutableStateOf("") } // New state for re-enter password
    val logo = painterResource(R.drawable.uwrizzlogo)
    // Initialize Firebase Auth
    val auth = FirebaseAuth.getInstance()
    val db = Firebase.firestore

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
        OutlinedTextField(
            value = reenterPassword,
            onValueChange = { reenterPassword = it },
            label = { Text("Re-enter Password") }, // Label for re-enter password field
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
                if (password != reenterPassword) {
                    Toast.makeText(context, "Passwords do not match.", Toast.LENGTH_SHORT).show()
                    return@Button
                }
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val user = auth.currentUser
                            // Send email verification link

                            addProfile(auth, db, firstname)
                            addPreference(auth, db)
                            addSurvey(auth, db)
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
                        }  else {
                            if ((task.exception as? FirebaseAuthException)?.errorCode == "ERROR_EMAIL_ALREADY_IN_USE") {
                                Toast.makeText(context, "Email is already registered. Please use a different email.", Toast.LENGTH_LONG).show()
                            } else {
                                Toast.makeText(context, "Authentication failed: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                            }
                            Log.w("CreateAccount", "createUserWithEmail:failure", task.exception)
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

fun addProfile(auth: FirebaseAuth, db: FirebaseFirestore, firstname: String) {
    val newUser = BasicUserInfo(
        userId = auth.currentUser?.uid as String,
        firstName = firstname,
        lastName = "",
        age = 18f,
        ethnicity = "",
        gender = "",
        program = "",
        oneWord = "",
        oneEmoji = "",
        programEmoji = "",
        hobbyEmoji = "",
        prompt = "",
        promptAnswer = ""
    )
    db.collection("users")
        .add(newUser)
        .addOnSuccessListener {
            Log.d("CreateAccount", "user-DocumentSnapshot successfully written!")
            // Handle success, navigate to next screen or perform other actions
        }
        .addOnFailureListener { e ->
            Log.w("CreateAccount", "user-Error writing document", e)
            // Handle failure
        }
}

fun addPreference(auth: FirebaseAuth, db: FirebaseFirestore) {
    val newPref = UserPreference(
        userId = auth.currentUser?.uid as String,
        interestedInGender = listOf(),
        interestedInEthnicity = listOf(),
        interestedInProgram = listOf(),
        agePreferenceMin = 18,
        agePreferenceMax= 30
    )
    db.collection("preferences")
        .add(newPref)
        .addOnSuccessListener {
            Log.d("CreateAccount", "pref-DocumentSnapshot successfully written!")
            // Handle success, navigate to next screen or perform other actions
        }
        .addOnFailureListener { e ->
            Log.w("CreateAccount", "pref-Error writing document", e)
            // Handle failure
        }
}

fun addSurvey(auth: FirebaseAuth, db: FirebaseFirestore) {
    val newSurvey = SurveyAnswers(
        userId = auth.currentUser?.uid as String,
        answers = List(10) { 3 }
    )
    db.collection("survey")
        .add(newSurvey)
        .addOnSuccessListener {
            Log.d("CreateAccount", "survey-DocumentSnapshot successfully written!")
            // Handle success, navigate to next screen or perform other actions
        }
        .addOnFailureListener { e ->
            Log.w("CreateAccount", "survey-Error writing document", e)
            // Handle failure
        }
}
private fun isValidEmail(email: String): Boolean {
    return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
}
    