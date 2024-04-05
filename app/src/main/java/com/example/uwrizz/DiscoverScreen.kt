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
    val auth = FirebaseAuth.getInstance()
    val currentUserId = auth.currentUser?.uid
    var profiles by remember { mutableStateOf<List<User>>(emptyList()) }
    var currentProfileIndex by remember { mutableStateOf(0) }

    // Fetch profiles once at the start and when the currentUserId changes
    LaunchedEffect(currentUserId) {
        currentUserId?.let { userId ->
            fetchProfiles(userId) { fetchedProfiles ->
                profiles = fetchedProfiles
            }
        }
    }

    // Function to handle like action
    fun likeProfile(profileId: String) {
        // Implement the Firestore logic to handle like action
        likeProfileInFirestore(currentUserId!!, profileId)
        // Move to next profile
        currentProfileIndex++
    }

    // Function to handle dislike action
    fun dislikeProfile() {
        // Optionally implement the Firestore logic to handle dislike action
        // For now, just move to next profile
        currentProfileIndex++
    }

    // Check if we've reached the end of profiles
    if (currentProfileIndex >= profiles.size) {
        Text("No more profiles")
        return
    }

    // Get the current profile to display
    val currentProfile = profiles[currentProfileIndex]

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        TopAppBar(
            title = { Text(currentProfile.firstName + " " + currentProfile.lastName) },
            modifier = Modifier.fillMaxWidth()
        )

        // Main content with profile info
        Box(
            modifier = Modifier
                .fillMaxSize()
                .weight(12f)
                .padding(16.dp)
        ) {
            ScrollableCard(
                currentProfile.program,
                currentProfile.programEmoji,
                currentProfile.hobby,
                currentProfile.hobbyEmoji,
                currentProfile.oneWord,
                currentProfile.oneEmoji,
                currentProfile.prompt,
                currentProfile.promptAnswer,
                currentProfile.firstName
            )
        }

        // Like and Dislike Buttons
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            XButton(onClick = { dislikeProfile() })
            HeartButton(onClick = { likeProfile(currentProfile.userId) })
        }
    }
}


@Composable
fun XButton(onClick: () -> Unit) {
    FloatingActionButton(
        onClick = onClick,
        modifier = Modifier
            .padding(16.dp),
        contentColor = Color(0xFFE1474E),
        containerColor = Color(0xFFF1F1F1),
    ) {
        Icon(Icons.Filled.Close, "Floating action button.")
    }
}

@Composable
fun HeartButton(onClick: () -> Unit) {
    FloatingActionButton(
        onClick = onClick,
        modifier = Modifier.padding(16.dp),
        contentColor = Color(0xFFE1474E),
        containerColor = Color(0xFFF1F1F1),
    ) {
        Icon(Icons.Filled.Favorite, "Like")
    }
}

@Composable
fun ScrollableCard(
    program: String, programEmoji: String, hobby: String, hobbyEmoji: String, oneWord: String, oneEmoji: String,
    prompt: String, promptAnswer: String, name: String
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
            PhotoCard(imageResourceId, 200.dp, name = name)
        }
    }
}

fun fetchProfiles(currentUserId: String, onResult: (List<User>) -> Unit) {
    Firebase.firestore.collection("users")
        .whereNotEqualTo("userId", currentUserId)
        .get()
        .addOnSuccessListener { result ->
            val users = result.documents.mapNotNull { it.toObject(User::class.java) }
            onResult(users)
        }
        .addOnFailureListener { exception ->
            Log.d("DiscoverScreen", "Error getting users: ", exception)
        }
}

fun likeProfileInFirestore(currentUserId: String, likedUserId: String) {
    val db = Firebase.firestore
    val currentUserDocRef = db.collection("users").document(currentUserId)

    // Using a transaction to safely add a liked user
    db.runTransaction { transaction ->
        val snapshot = transaction.get(currentUserDocRef)
        val currentLikes = snapshot.get("likes") as? List<String> ?: listOf()
        if (likedUserId !in currentLikes) {
            transaction.update(currentUserDocRef, "likes", currentLikes + likedUserId)
        }
    }.addOnSuccessListener {
        Log.d("DiscoverScreen", "Profile successfully liked: $likedUserId")
    }.addOnFailureListener { e ->
        Log.w("DiscoverScreen", "Error liking profile: $likedUserId", e)
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
            Spacer(modifier = Modifier.height(10.dp))
            // Floating heart button (bottom right)
//            FloatingActionButton(
//                onClick = { /* Add your onClick logic here */ },
//                modifier = Modifier
//                    .align(Alignment.BottomEnd)
//                    .padding(5.dp)
//                    .size(40.dp),
//                contentColor = Color(0xFFE1474E),
//                containerColor = Color(0xFFF1F1F1), // Set the background color here
//                content = {
//                    Icon(Icons.Filled.Favorite, "Favorite")
//                },
//                // Set the size of the FAB here
//                // You can adjust the size as needed
//                // For example, to make it smaller, reduce the size value
//            )
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
