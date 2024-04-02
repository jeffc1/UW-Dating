package com.example.uwrizz
import android.net.Uri

data class User(
    val firstname: String,
    val lastname: String,
    val imageuri: Uri?,
    val imageuri1: Uri?,
    val imageuri2: Uri?,
    val imageuri3: Uri?,
    val bio: String,
    val hobby: String,
    val job: String,
    val gender: String,
    val program: String,
)