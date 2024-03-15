package com.example.uwrizz

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp


// ALVINS STUFF

//@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun PreferencesScreen(onNavigate: (PreferenceType) -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Preferences", color = Color.White) },
//                backgroundColor = Color(0xFF000000) // Black
            )
        },
        content = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 8.dp)
            ) {
                PreferenceItem(title = "Gender", summary = "Man", onClick = { onNavigate(PreferenceType.Gender) })
                PreferenceItem(
                    title = "I'm interested in",
                    summary = "Women",
                    onClick = { onNavigate(PreferenceType.Interest) })
                PreferenceItem(
                    title = "Program",
                    summary = "Not Engineering",
                    onClick = { onNavigate(PreferenceType.Neighborhood) })
                PreferenceItem(
                    title = "Age range",
                    summary = "18 - 23",
                    onClick = { onNavigate(PreferenceType.AgeRange) })
                PreferenceItem(
                    title = "Height",
                    summary = "3'0\" - 7'0\"",
                    onClick = { onNavigate(PreferenceType.Height) })
            }
        }
    )
}

@Composable
fun PreferenceItem(title: String, summary: String, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp)
    ) {
        Text(title, fontWeight = FontWeight.Bold)
        Text(summary, color = Color.Gray)
        Divider(color = Color.LightGray, thickness = 1.dp, modifier = Modifier.padding(top = 8.dp))
    }
}

enum class PreferenceType {
    Gender, Interest, Neighborhood, AgeRange, Distance, Height, Ethnicity, Religion
}


@Preview(showBackground = true)
@Composable
fun PreferencesScreenPreview() {
    PreferencesScreen(onNavigate = {})
}
