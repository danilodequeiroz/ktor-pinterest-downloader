package com.github.danilodequeiroz.pinterestdl

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {

    routing {
        get("/") {
            call.respondText("Hello World!")
        }

        get("/download") {
            val pinterestUrl = call.request.queryParameters["url"]

            if (pinterestUrl.isNullOrEmpty()) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    MediaLink("", "", false, "URL query parameter is missing or empty.")
                )
                return@get
            }

            val pinterstScraper = PinterestImpl(pinterestUrl)
            val mediaLink = pinterstScraper.getMediaLink()

            if (mediaLink.success) {
                call.respond(HttpStatusCode.OK, mediaLink)
            } else {
                call.respond(HttpStatusCode.BadRequest, mediaLink)
            }
        }
    }
}

