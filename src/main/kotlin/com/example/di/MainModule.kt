package com.example.di

import com.example.AppSecrets
import com.example.data.MessageDataSource
import com.example.data.MessageDataSourceImpl
import com.example.email.EmailRepo
import com.example.email.service.DefaultEmailService
import com.example.email.service.EmailService
import com.example.room.RoomController
import com.mongodb.kotlin.client.coroutine.MongoClient
import com.typesafe.config.ConfigFactory
import org.koin.dsl.module
import org.simplejavamail.api.mailer.Mailer
import org.simplejavamail.api.mailer.config.TransportStrategy
import org.simplejavamail.mailer.MailerBuilder

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

    // mailer done
    single<Mailer> {
        MailerBuilder
            .withSMTPServer(AppSecrets.SMTP_SERVER_HOST, AppSecrets.SMTP_SERVER_PORT)
            .withTransportStrategy(TransportStrategy.SMTP_TLS)
            .withSMTPServerUsername(AppSecrets.SMTP_SERVER_USER_NAME)
            .withSMTPServerPassword(AppSecrets.SMTP_SERVER_PASSWORD)
            .buildMailer()

    }

    // email service
    single<EmailService> {
        DefaultEmailService(
            mailer = get()
        )
    }

    // Email repo
    single<EmailRepo>{
        EmailRepo(
            emailService = get()
        )
    }
}