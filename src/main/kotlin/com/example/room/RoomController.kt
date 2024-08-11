package com.example.room

import com.example.data.MessageDataSource
import com.example.data.model.Message
import com.example.data.model.Users
import io.ktor.websocket.Frame
import io.ktor.websocket.close
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.concurrent.ConcurrentHashMap

class RoomController(
    private val messageDataSource: MessageDataSource,
) {
    private val members = ConcurrentHashMap<String, Member>()

    fun join(member: Member) {
        if (members.containsKey(member.userName)) {
            throw MemberAlreadyExistException()
        }
        members[member.userName] = Member(member.userName, member.sessionId, member.sockets)
    }

    fun getAllMembers(): List<Users> {
        return members.keys().toList().map {
            Users(
                name = it,
                sessionId = members[it]?.sessionId.toString()
            )
        }
    }


    suspend fun sendMessage(senderUserName: String, message: String) {
        val messageEntity = Message(
            text = message,
            user = senderUserName,
            timeStamp = System.currentTimeMillis()
        )
        messageDataSource.insertMessage(messageEntity)
        members.values.forEach { member ->
            val parsedMessage = Json.encodeToString(messageEntity)
            member.sockets.send(Frame.Text(parsedMessage))
        }
    }

    suspend fun getAllMessages(): List<Message> {
        return messageDataSource.getAllMessages()
    }

    suspend fun tryDisconnect(userName: String) = coroutineScope {
        val user = members[userName] ?: return@coroutineScope // Handle null user
        val sockets = user.sockets

        launch {
            try {
                val userData = Json.encodeToString(Users(userName, "Disconnected"))
                sockets.send(Frame.Text(userData))
            } catch (e: Exception) {
                // Handle send error
                println("Error sending disconnect message: $e")
            } finally {
                try {
                    sockets.close()
                } catch (e: Exception) {
                    // Handle close error
                    println("Error closing WebSocket: $e")
                }
                members.remove(userName)
            }
        }
    }
}