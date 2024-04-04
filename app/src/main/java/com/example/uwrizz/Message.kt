package com.example.uwrizz

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive

import kotlinx.serialization.*

import kotlinx.serialization.SerialName

data class Message(
    val id: String = "",
    val receiverId: String = "",
    val senderId: String = "",
    val text: String = "",
    val timestamp: Long = 0L, // You can also use com.google.firebase.Timestamp
    val chatId: String = ""
)

