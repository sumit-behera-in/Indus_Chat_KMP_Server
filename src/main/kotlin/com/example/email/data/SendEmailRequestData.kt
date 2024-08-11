package com.example.email.data

import kotlinx.serialization.Serializable

@Serializable
data class SendEmailRequestData(
    val email:String,
    val subject:String,
    val message:String
)