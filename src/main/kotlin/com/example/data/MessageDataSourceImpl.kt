package com.example.data

import com.example.data.model.Message
import com.mongodb.client.model.Sorts.ascending
import com.mongodb.kotlin.client.coroutine.MongoDatabase
import kotlinx.coroutines.flow.toList

class MessageDataSourceImpl(
    db: MongoDatabase,
) : MessageDataSource {

    private val messages = db.getCollection<Message>("messages")

    override suspend fun getAllMessages(): List<Message> {
        return messages.find()
            .sort(ascending("timestamp"))
            .toList()
    }

    override suspend fun insertMessage(message: Message) {
        messages.insertOne(message)
    }
}