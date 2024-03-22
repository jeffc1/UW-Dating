package com.example.uwrizz

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive

import kotlinx.serialization.*

import kotlinx.serialization.SerialName

@Serializable
data class Message(
    @SerialName("id") val id: String,
    val text: String,
    val username: String,
    val timestamp: Long
)
