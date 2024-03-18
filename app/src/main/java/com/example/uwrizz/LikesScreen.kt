package com.example.uwrizz

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource

val exampleProfiles = listOf(
    Profile(name = "LeBron James", imageResourceId = R.drawable.walterwhite),
    Profile(name = "Walter White", imageResourceId = R.drawable.walterwhite),
    Profile(name = "Jordan Belfort", imageResourceId = R.drawable.walterwhite),
    Profile(name = "Bruce Wayne", imageResourceId = R.drawable.walterwhite),
    Profile(name = "Peter Parker", imageResourceId = R.drawable.walterwhite),
)

@Composable
fun LikesScreen(profiles: List<Profile>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        if (profiles.size % 2 == 0) {
            items(profiles.chunked(2)) { rowItems ->
                Row(Modifier.fillMaxWidth()) {
                    rowItems.forEach { profile ->
                        ProfileCard(profile = profile, modifier = Modifier.weight(1f))
                    }
                }
            }
        }
        else {
            item {
                ProfileCard(profile = profiles[0], modifier = Modifier.fillMaxWidth())
            }
            items(profiles.drop(1).chunked(2)) { rowItems ->
                Row(Modifier.fillMaxWidth()) {
                    rowItems.forEach { profile ->
                        ProfileCard(profile = profile, modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileCard(profile: Profile, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .padding(8.dp)
            .aspectRatio(1f),
        shape = RoundedCornerShape(16.dp),
        elevation = 4.dp
    ) {
        Column(
            verticalArrangement = Arrangement.Bottom,
            modifier = Modifier.fillMaxSize()
        ) {
            Image(
                painter = painterResource(id = profile.imageResourceId),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )
            Text(
                text = profile.name,
                style = MaterialTheme.typography.subtitle1,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            )
        }
    }
}

data class Profile(val name: String, val imageResourceId: Int)