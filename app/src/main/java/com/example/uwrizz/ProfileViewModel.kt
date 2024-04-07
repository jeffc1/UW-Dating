package com.example.uwrizz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class UserProfileViewModel(private val profileRepository: ProfileRepository) : ViewModel() {

    // Function to save basic user information
    fun saveBasicUserInfo(basicUserInfo: BasicUserInfo) {
        viewModelScope.launch {
            profileRepository.saveBasicUserInfo(basicUserInfo) { success, message ->
            }
        }
    }

    // Function to save user preferences
    fun saveUserPreferences(userPreferences: UserPreference) {
        viewModelScope.launch {
            profileRepository.saveUserPreference(userPreferences) { success, message ->
                // Update UI state or LiveData
            }
        }
    }

    // Function to save survey responses
    fun saveSurveyAnswers(surveyAnswers: SurveyAnswers) {
        viewModelScope.launch {
            profileRepository.saveSurveyAnswers(surveyAnswers) { success, message ->
                // Update UI state or LiveData
            }
        }
    }
}
