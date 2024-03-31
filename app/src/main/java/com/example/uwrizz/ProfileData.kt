package com.example.uwrizz

import com.example.uwrizz.ui.theme.UWRizzTheme
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseAuth

data class BasicUserInfo(
    // Firebase requires a no-argument constructor for Data classes, so provide default values.
    val userId: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val age: Int = 0,
    val ethnicity: String = "",
    val gender: String = "",
    val program: String = "",
    val job: String = "",
    val bio: String = ""
)

data class UserPreference(
    val userId: String = "",
    val interestedInGender: String = "",
    val interestedInEthnicity: String = "",
    val interestedInProgram: String = "",
    val agePreferenceMin: Int = 0,
    val agePreferenceMax: Int = 0
)

data class SurveyAnswers(
    val userId: String = "",
    val answers: Map<String, String> = emptyMap()
)

class ProfileRepository {

    private val db = Firebase.firestore // Firestore instance

    fun saveBasicUserInfo(userInfo: BasicUserInfo, onComplete: (Boolean, String) -> Unit) {
        db.collection("basicUserInfo").document(userInfo.userId)
            .set(userInfo)
            .addOnSuccessListener { onComplete(true, "User profile saved successfully.") }
            .addOnFailureListener { exception -> onComplete(false, exception.localizedMessage ?: "Error saving profile.") }
    }

    fun saveUserPreference(userPreference: UserPreference, onComplete: (Boolean, String) -> Unit) {
        db.collection("userPreferences").document(userPreference.userId)
            .set(userPreference)
            .addOnSuccessListener { onComplete(true, "Preferences saved successfully.") }
            .addOnFailureListener { exception -> onComplete(false, exception.localizedMessage ?: "Error saving preferences.") }
    }

    fun saveSurveyAnswers(surveyAnswers: SurveyAnswers, onComplete: (Boolean, String) -> Unit) {
        db.collection("surveyAnswers").document(surveyAnswers.userId)
            .set(surveyAnswers)
            .addOnSuccessListener { onComplete(true, "Survey answers saved successfully.") }
            .addOnFailureListener { exception -> onComplete(false, exception.localizedMessage ?: "Error saving survey answers.") }
    }

    // Additional functions for retrieving and updating data can be added here.
}
