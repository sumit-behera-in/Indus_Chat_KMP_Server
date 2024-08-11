package com.example.email

import com.example.AppSecrets
import com.example.email.data.EmailData
import com.example.email.data.SendEmailRequestData
import com.example.email.data.SendEmailResponse
import com.example.email.service.EmailService

class EmailRepo(private val emailService: EmailService) {
    suspend fun sendEmail(data: SendEmailRequestData): SendEmailResponse {
        val result = emailService.sendEmail(
            EmailData(
                emailTo = data.email,
                subject = data.subject,
                message = data.message,
                emailFrom = AppSecrets.EMAIL_FROM
            )
        )
        return SendEmailResponse(
            success = result,
            status = if (result) 200 else 400,
            message = if (result) "Successfully sent email" else "Failed to send email"
        )
    }
}