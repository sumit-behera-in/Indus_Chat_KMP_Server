package com.example.email.service

import com.example.email.data.EmailData
import org.simplejavamail.api.mailer.Mailer
import org.simplejavamail.email.EmailBuilder

class DefaultEmailService(
    private val mailer: Mailer
) : EmailService {
    override suspend fun sendEmail(data: EmailData): Boolean {
        val userName = data.emailTo.split("@")[0]
        val email = EmailBuilder.startingBlank()
            .from("Chat Server", data.emailFrom)
            .to(userName, data.emailTo)
            .withSubject(data.subject)
            //.withPlainText(data.message)
            .withHTMLText(data.message)
            .buildEmail()
        return try {
            mailer.sendMail(email)
            true
        }catch (e:Exception){
            e.printStackTrace()
            false
        }
    }
}