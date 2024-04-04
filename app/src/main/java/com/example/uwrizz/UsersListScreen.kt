package com.example.uwrizz

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
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
    val users = remember { mutableStateListOf<User>() } // Replace with actual user fetching logic
    val firestore = Firebase.firestore

    // Fetch users from Firestore
    LaunchedEffect(key1 = "users_list") {
        firestore.collection("users")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    // Assuming 'User' corresponds to your Firestore user document structure
                    val user = document.toObject<User>()
                    if (user.userId != loggedInUserId) { // Exclude the current logged-in user from the list
                        users.add(user)
                    }
                }
            }
    }

    LazyColumn {
        items(users) { user ->
            UserCard(user, onUserClicked)
        }
    }
}

//@Composable
//fun UserListItem(user: User, onUserClicked: (String) -> Unit) {
//    // This could be your list item layout, for simplicity, let's make it a Text
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