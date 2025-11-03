package com.github.danilodequeiroz.pinterestdl

import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.*
import io.ktor.server.cio.*
import io.ktor.server.plugins.contentnegotiation.*

fun main(args: Array<String>) {
    EngineMain.main(args)
}

fun Application.module() {
    install(ContentNegotiation){
        json()
    }
    configureRouting()
}
