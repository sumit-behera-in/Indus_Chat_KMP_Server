package com.example.room

import com.example.data.MessageDataSource
import com.example.data.model.Message
import io.ktor.websocket.Frame
import io.ktor.websocket.close
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


    suspend fun sendMessage(senderUserName: String, message: String) {
        members.values.forEach { member ->
            val messageEntity = Message(
                text = message,
                user = senderUserName,
                timeStamp = System.currentTimeMillis()
            )

            messageDataSource.insertMessage(messageEntity)

            val parsedMessage = Json.encodeToString(messageEntity)
            member.sockets.send(Frame.Text(parsedMessage))
        }
    }

    suspend fun getAllMessages(): List<Message> {
        return messageDataSource.getAllMessages()
    }

    suspend fun tryDisconnect(userName: String) {
        members[userName]?.sockets?.close()
        if (members.containsKey(userName)) {
            members.remove(userName)
        }
    }
}