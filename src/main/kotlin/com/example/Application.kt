package com.example

import com.example.di.mainModule
import com.example.plugins.configureMonitoring
import com.example.plugins.configureRouting
import com.example.plugins.configureSecurity
import com.example.plugins.configureSerialization
import com.example.plugins.configureSockets
import io.ktor.server.application.Application
import io.ktor.server.application.install
import org.koin.ktor.plugin.Koin

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {

    install(Koin) {
        modules(mainModule)
    }

    configureSockets()
    configureSerialization()
    //configureDatabases()
    configureMonitoring()
    configureSecurity()
    configureRouting()
}
