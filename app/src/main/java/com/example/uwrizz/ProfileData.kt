package com.example.uwrizz

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

data class BasicUserInfo(
    // Firebase requires a no-argument constructor for Data classes, so provide default values.
    val userId: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val age: Int = 0,
    val ethnicity: String = "",
    val gender: String = "",
    val program: String = "",
    val programEmoji: String = "",
    val hobby: String = "",
    val hobbyEmoji: String = "",
    val oneWord: String = "",
    val oneEmoji: String = "",
    val prompt: String = "",
    val promptAnswer: String = "",
    val profilePictureUri: String = "", // For the main profile picture
    val pictureUri1: String = "", // For the first additional picture
    val pictureUri2: String = "", // For the second additional picture
    val pictureUri3: String = "", // For the third additional picture
    val likes: List<String>? = null
)


data class UserPreference(
    val userId: String = "",
    val interestedInGender: List<String> = listOf(),
    val interestedInEthnicity: List<String> = listOf(),
    val interestedInProgram: List<String> = listOf(),
    val agePreferenceMin: Int = 0,
    val agePreferenceMax: Int = 0
)

data class SurveyAnswers(
    val userId: String = "",
    val answers: List<Int> = listOf()
)