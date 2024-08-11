package com.example.email.service

import com.example.email.data.EmailData

interface EmailService {
    suspend fun sendEmail(data: EmailData):Boolean
}