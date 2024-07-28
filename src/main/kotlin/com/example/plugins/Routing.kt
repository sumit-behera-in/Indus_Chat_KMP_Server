package com.example.plugins

import com.example.room.RoomController
import com.example.routes.chatSockets
import com.example.routes.getAllMembers
import com.example.routes.getAllMessages
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import org.koin.ktor.ext.inject

fun Application.configureRouting() {

    val roomController by inject<RoomController>()

    routing {
        get("/") {
            call.respondText("welcome to sample app")
        }
        chatSockets(roomController)
        getAllMessages(roomController)
        getAllMembers(roomController)
    }
}
