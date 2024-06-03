package com.example.di

import com.example.data.MessageDataSource
import com.example.data.MessageDataSourceImpl
import com.example.room.RoomController
import com.mongodb.kotlin.client.coroutine.MongoClient
import com.typesafe.config.ConfigFactory
import org.koin.dsl.module

val mainModule = module {
    val config = ConfigFactory.load()

    // Fetch MongoDB URI and database name from the configuration
    val mongoUri = config.getString("ktor.mongodb.uri")
        ?: throw RuntimeException("Failed to access MongoDB URI.")
    val databaseName = config.getString("ktor.mongodb.database")
        ?: throw RuntimeException("Failed to access MongoDB database name.")

    // MongoDB client
    single {
        MongoClient.create(connectionString = mongoUri)
    }

    // Database instance
    single {
        get<MongoClient>().getDatabase(databaseName)
    }

    // Message data source
    single<MessageDataSource> {
        MessageDataSourceImpl(get())
    }

    // Room controller
    single {
        RoomController(get())
    }
}