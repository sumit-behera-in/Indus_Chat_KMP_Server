package com.example.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Users(
    val name: String,
    val sessionId: String,
)
