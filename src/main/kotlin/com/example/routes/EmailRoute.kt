package com.example.routes

import com.example.email.EmailRepo
import com.example.email.data.SendEmailRequestData
import com.example.email.data.SendEmailResponse
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.request.receiveNullable
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post

fun Route.emailRoute(emailRepo: EmailRepo) {
    post("send-email") {
        val requestData = call.receiveNullable<SendEmailRequestData>()
            ?: return@post call.respond(
                HttpStatusCode.BadRequest,
                SendEmailResponse(
                    status = 400,
                    success = false,
                    message = "Missing request params"
                )
            )
        val responseData = emailRepo.sendEmail(requestData)
        call.respond(
            if (responseData.success) HttpStatusCode.OK else HttpStatusCode.BadRequest,
            responseData
        )
    }
}