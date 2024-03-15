package com.example.uwrizz

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun LikesScreen(onNavigate: (LikeType) -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Likes", color = Color.Black) },
//                backgroundColor = Color(0xFF000000) // Black
            )
        },
        content = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 8.dp)
            ) {
                LikeItem(title = "Books", summary = "Book1, Book2", onClick = { onNavigate(LikeType.Books) })
                LikeItem(
                    title = "Books",
                    summary = "Harry Potter and the Philosopher's Stone",
                    onClick = { onNavigate(LikeType.Books) })
                LikeItem(
                    title = "Movies",
                    summary = "Guardians of the Galaxy Vol 3",
                    onClick = { onNavigate(LikeType.Movies) })
                LikeItem(title = "Sports", summary = "Swimming, biking", onClick = { onNavigate(LikeType.Sports) })
                LikeItem(title = "Music", summary = "Taylor Swift", onClick = { onNavigate(LikeType.Music) })
                LikeItem(title = "Food", summary = "Sushi", onClick = { onNavigate(LikeType.Foods) })
            }
        }
    )
}

@Composable
fun LikeItem(title: String, summary: String, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp)
    ) {
        Text(title, fontWeight = FontWeight.Bold)
        Text(summary, color = Color.Gray)
    }
}

enum class LikeType {
    Books, Movies, Sports, Music, Foods
}