package com.example.routes

import com.example.data.model.Users
import com.example.room.Member
import com.example.room.MemberAlreadyExistException
import com.example.room.RoomController
import com.example.session.ChatSession
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.sessions.get
import io.ktor.server.sessions.sessions
import io.ktor.server.websocket.webSocket
import io.ktor.websocket.CloseReason
import io.ktor.websocket.Frame
import io.ktor.websocket.close
import io.ktor.websocket.readText
import kotlinx.coroutines.channels.consumeEach
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json


fun Route.chatSockets(roomController: RoomController) {
    webSocket("/chat-socket") {
        val userName = call.request.queryParameters["userName"] ?: "Guest"
        val session = call.sessions.get<ChatSession>()?.copy(
            userName = userName
        )

        if (session == null) {
            close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "No session"))
            return@webSocket
        }

        try {
            val member = Member(
                userName = session.userName,
                sessionId = session.sessionId,
                sockets = this
            )
            roomController.join(member)

            val userData = Json.encodeToString(Users(session.userName,"Connected"))
            outgoing.send(Frame.Text(userData))

            incoming.consumeEach { frame ->
                if (frame is Frame.Text) {
                    roomController.sendMessage(
                        senderUserName = session.userName,
                        message = frame.readText()
                    )

                    println("name = ${session.userName} \n message = ${frame.readText()}")
                }
            }

        } catch (e: MemberAlreadyExistException) {
            call.respond(HttpStatusCode.Conflict)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            val userData = Json.encodeToString(Users(session.userName,"disConnected"))
            outgoing.send(Frame.Text(userData))
            //roomController.tryDisconnect(session.userName)
        }
    }
}

fun Route.getAllMembers(roomController: RoomController) {
    get("/users") {
        call.respond(
            HttpStatusCode.OK,
            roomController.getAllMembers()
        )
    }
}


fun Route.getAllMessages(roomController: RoomController) {
    get("/messages") {
        call.respond(
            HttpStatusCode.OK,
            roomController.getAllMessages()
        )
    }

}
