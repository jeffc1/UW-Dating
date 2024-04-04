package com.example.uwrizz

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Image
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.saveable.rememberSaveable
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainContent() {
    val db = Firebase.firestore
    val auth = FirebaseAuth.getInstance()
    var firstname by rememberSaveable { mutableStateOf("") }
    var lastname by rememberSaveable { mutableStateOf("") }
    var hobby by rememberSaveable { mutableStateOf("") }
    var hobbyEmoji by rememberSaveable { mutableStateOf("") }
    var program by rememberSaveable { mutableStateOf("") }
    var programEmoji by rememberSaveable { mutableStateOf("") }
    var age by rememberSaveable { mutableStateOf(18f) }
    var oneWord by rememberSaveable { mutableStateOf("") }
    var oneEmoji by rememberSaveable { mutableStateOf("") }
    var prompt by rememberSaveable { mutableStateOf("") }
    var promptAnswer by rememberSaveable { mutableStateOf("") }

    remember {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            val usersCollection = db.collection("users")
            val userRef = usersCollection.whereEqualTo("userId", userId)

            userRef.get()
                .addOnSuccessListener { querySnapshot ->
                    if (!querySnapshot.isEmpty) {
                        val user =
                            querySnapshot.documents[0].toObject(BasicUserInfo::class.java)
                        if (user != null) {
                            // Update mutable state variables with user data
                            firstname = user.firstName
                            lastname = user.lastName
                            age = user.age
                            hobby = user.hobby
                            hobbyEmoji = user.hobbyEmoji
                            program = user.program
                            programEmoji = user.programEmoji
                            oneWord = user.oneWord
                            oneEmoji = user.oneEmoji
                            prompt = user.prompt
                            promptAnswer = user.promptAnswer
                        }
                    } else {
                        Log.d(
                            "ProfileSettingsScreen",
                            "No matching document found for the userId"
                        )
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d("ProfileSettingsScreen", "get failed with ", exception)
                }
        } else {
            Log.d("ProfileSettingsScreen", "User not authenticated or UID is null")
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        // Your main content goes here
        TopAppBar(
            title = { Text("Walter") },
            modifier = Modifier.fillMaxWidth()
        )

        // Add a spacer to push the buttons to the bottom

        // Row of 4 buttons at the bottom
        Box(
            modifier = Modifier
                .fillMaxSize()
                .weight(12f)
                .padding(16.dp)
        ) {
            ScrollableCard(program, programEmoji, hobby, hobbyEmoji, oneWord, oneEmoji, prompt, promptAnswer)
        }
        XButton()
    }
}

@Composable
fun XButton() {
    FloatingActionButton(
        onClick = { },
        modifier = Modifier
            .padding(16.dp),
        contentColor = Color(0xFFE1474E),
        containerColor = Color(0xFFF1F1F1),
    ) {
        Icon(Icons.Filled.Close, "Floating action button.")
    }
}

@Composable
fun ScrollableCard(
    program: String, programEmoji: String, hobby: String, hobbyEmoji: String, oneWord: String, oneEmoji: String,
    prompt: String, promptAnswer: String
) {
    LazyColumn(
        modifier = Modifier
            .padding(bottom = 5.dp)
    ) {
        // Larger square card at the bottom for a photo
        item {
            LazyRow(
                modifier = Modifier.padding(bottom = 5.dp)
            ) {
                item {
                    InfoCard("Studying", programEmoji, program)
                }
                item {
                    InfoCard("Hobby", hobbyEmoji, hobby)
                }
                item {
                    InfoCard("One Word:", oneEmoji, oneWord)
                }
            }
            CustomCard(prompt, promptAnswer)
            val imageResourceId = painterResource(id = R.drawable.walterwhite) // Obtaining resource ID
            PhotoCard(imageResourceId, 200.dp, name = "Walter")
        }
    }
}

@Composable
fun PhotoCard(image: Painter, imageSize: Dp, cardPadding: PaddingValues = PaddingValues(10.dp), name: String = "") {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp), // Add vertical padding to match other cards
        shape = MaterialTheme.shapes.medium,
    ) {
        Box(
            modifier = Modifier
                .aspectRatio(1f) // Set 1:1 aspect ratio
                .padding(cardPadding) // Add padding
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Image with ContentScale.Crop to maintain aspect ratio
                Image(
                    painter = image,
                    contentDescription = "Photo",
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentScale = ContentScale.Crop // Crop the image to fit the 1:1 aspect ratio
                )

                // Name text at the bottom
                if (name.isNotEmpty()) {
                    Text(
                        text = name,
                        modifier = Modifier
                            .padding(start = 12.dp, bottom = 8.dp)
                            .align(Alignment.Start),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
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
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column {
                // Title (left-justified, not bold)
                Text(
                    text = title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(bottom = 8.dp),
                    textAlign = TextAlign.Start,
                    fontWeight = FontWeight.Normal, // Not bold
                    fontSize = 14.sp
                )

                // Content (left-justified, bold, bigger)
                Text(
                    text = content,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 40.dp)
                        .wrapContentHeight(),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp // Adjust the size as needed
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            // Floating heart button (bottom right)
            FloatingActionButton(
                onClick = { /* Add your onClick logic here */ },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(5.dp)
                    .size(40.dp),
                contentColor = Color(0xFFE1474E),
                containerColor = Color(0xFFF1F1F1), // Set the background color here
                content = {
                    Icon(Icons.Filled.Favorite, "Favorite")
                },
                // Set the size of the FAB here
                // You can adjust the size as needed
                // For example, to make it smaller, reduce the size value
            )
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
