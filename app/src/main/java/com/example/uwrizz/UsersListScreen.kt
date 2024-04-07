package com.example.uwrizz

import android.util.Log
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.toObject
import com.google.firebase.ktx.Firebase


@Composable
fun UsersListScreen(loggedInUserId: String, onUserClicked: (String) -> Unit) {
    val mutualLikes = remember { mutableStateListOf<User>() }
    val firestore = Firebase.firestore
    var message by remember { mutableStateOf("") }

    LaunchedEffect(key1 = "mutual_likes") {
        // Fetch the list of users that the logged-in user has liked
        firestore.collection("users").whereEqualTo("userId", loggedInUserId).get()
            .addOnSuccessListener { loggedInUserSnapshot ->
                // Extract the first document assuming there's only one user with that userId
                val loggedInUserDocument = loggedInUserSnapshot.documents.firstOrNull()
                val loggedInUser = loggedInUserDocument?.toObject<User>()
                val usersLikedByLoggedInUser = loggedInUser?.likes ?: emptyList()

                if (usersLikedByLoggedInUser.isNotEmpty()) {
                    // Fetch the list of users who have liked the logged-in user
                    firestore.collection("users").whereIn("userId", usersLikedByLoggedInUser).get()
                        .addOnSuccessListener { usersWhoLikedSnapshot ->
                            val usersWhoLikedLoggedInUser = usersWhoLikedSnapshot.documents.mapNotNull { it.toObject<User>() }
                            // Intersect the userIds to find mutual likes
                            val mutualLikesIds = usersWhoLikedLoggedInUser.filter { it.likes?.contains(loggedInUserId) == true }
                                .map { it.userId }

                            if (mutualLikesIds.isNotEmpty()) {
                                firestore.collection("users").whereIn("userId", mutualLikesIds).get()
                                    .addOnSuccessListener { mutualLikesResult ->
                                        for (document in mutualLikesResult) {
                                            val user = document.toObject<User>()
                                            mutualLikes.add(user)
                                        }
                                    }
                            } else {
                                message = "No mutual likes yet. Start swiping to find matches!"
                            }
                        }
                } else {
                    message = "You haven't liked anyone yet. Start exploring profiles to find people you like!"
                }
            }
            .addOnFailureListener {
                message = "An error occurred while fetching your likes."
            }
    }

    Column {
        if (mutualLikes.isNotEmpty()) {
            LazyColumn {
                items(mutualLikes) { user ->
                    UserCard(user, onUserClicked)
                }
            }
        } else {
            Text(text = message, modifier = Modifier.padding(16.dp))
        }
    }
}



//@Composable
//fun UserListItem(user: User, onUserClicked: (String) -> Unit) {
//    Text(
//        text = ""+user.firstName + ": " + user.userId,
//        modifier = Modifier
//            .fillMaxWidth()
//            .clickable { onUserClicked(user.userId) }
//            .padding(16.dp)
//    )
//}

@Composable
fun UserCard(user: User, onUserClicked: (String) -> Unit) {

    Card(
        modifier = Modifier
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .fillMaxWidth()
            .clickable { onUserClicked(user.userId) }
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.AccountBox, // Or any other icon you'd like
                contentDescription = null, // Decorative image, no description needed
                modifier = Modifier
                    .size(40.dp)
                    .border(1.5.dp, Color.Gray, CircleShape)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = user.firstName, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }
        }
    }
}