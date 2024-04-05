package com.example.uwrizz
import android.net.Uri

data class User(
    val firstName: String = "",
    val email: String = "",
    val password: String = "",
    val userId: String = "",
    val lastName: String = "",
    val gender: String = "",
    val ethnicity: String = "",
    val hobby: String = "",
    val age: Int = 18,
    val likes: List<String> = listOf(),
    val hobbyEmoji: String = "",
    val oneWord: String = "",
    val oneEmoji: String = "",
    val pictureUri1: String = "",
    val pictureUri2: String = "",
    val pictureUri3: String = "",
    val profilePictureUri: String = "",
    val program: String = "",
    val programEmoji: String = "",
    val prompt: String = "",
    val promptAnswer: String = ""
)
