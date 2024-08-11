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
                message = generateEmailContent(),
                emailFrom = AppSecrets.EMAIL_FROM
            )
        )
        return SendEmailResponse(
            success = result,
            status = if (result) 200 else 400,
            message = if (result) "Successfully sent email" else "Failed to send email"
        )
    }


    private fun generateEmailContent(): String {
        val chars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
        val otp:String = (1..6) .map { chars.random() }.joinToString("")
        return """
        <html>
        <body>
            <p>Dear User,</p> <br>
            <p>Your One Time Password (OTP) for login: </p><br> 
            <center><b style="font-size: 1.5em;"><font color="red">$otp</font></b></center>
            <p>Please note that the OTP is valid for only one session</p><br>
            <p>Regards,</p>
            <p><b>Team ChatServer</b></p>
            <a href="mailto:s1508b@gmail.com">s1508b@gmail.com</a>
        </body>
        </html>
    """.trimIndent()
    }

}