package com.example.uwrizz

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun MainContent() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Your main content goes here

        // Add a spacer to push the buttons to the bottom
        Spacer(modifier = Modifier.weight(1f))

        // Row of 4 buttons at the bottom
        Box(
            modifier = Modifier
                .fillMaxSize()
                .weight(12f)
        ) {
            ScrollableCard()
            XButton()
        }
    }
}

@Composable
fun XButton() {
    FloatingActionButton(
        onClick = { },
        modifier = Modifier
            .padding(16.dp)
    ) {
        Icon(Icons.Filled.Close, "Floating action button.")
    }
}

@Composable
fun ScrollableCard() {
    LazyColumn(
        modifier = Modifier
            .padding(bottom = 5.dp)
    ) {
        // Larger square card at the bottom for a photo
        item {
            InfoCard("Studying", "💻","CompEng")
            InfoCard("Fav Sport", "🎾", "Tennis :)")
            CustomCard("A typical sunday", "running 5 marathons in 5 countries")
            CustomCard("Green flags I look for", "loving Netflix!")
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
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Title (left-justified, not bold)
            Text(
                text = title,
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(bottom = 8.dp),
                textAlign = TextAlign.Start,
                fontWeight = FontWeight.Normal, // Not bold
                fontSize = 10.sp
            )

            // Content (left-justified, bold, bigger)
            Text(
                text = content,
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(bottom = 16.dp),
                textAlign = TextAlign.Start,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp // Adjust the size as needed
            )

            // Heart emoji button (bottom right)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(top = 8.dp)
                    .align(Alignment.End)
            ) {
                Spacer(modifier = Modifier.weight(1f))
                IconButton(
                    onClick = {
                        // Add your onClick logic here for the heart button
                    }
                ) {
                    Text("❤️") // Heart emoji
                }
            }
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
