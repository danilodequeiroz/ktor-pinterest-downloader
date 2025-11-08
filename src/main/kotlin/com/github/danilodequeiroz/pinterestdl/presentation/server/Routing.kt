package com.github.danilodequeiroz.pinterestdl.presentation.server

import com.github.danilodequeiroz.pinterestdl.LogToObservabilityImpl
import com.github.danilodequeiroz.pinterestdl.data.parser.EMPTY_STRING
import com.github.danilodequeiroz.pinterestdl.data.parser.PinterestHtmlParserImpl
import com.github.danilodequeiroz.pinterestdl.data.repository.PinterestHttpScrapingRepositoryImp
import com.github.danilodequeiroz.pinterestdl.data.repository.RepositoryResult
import com.github.danilodequeiroz.pinterestdl.data.repository.source.remote.PinterestKtorHttpClientDataSourceImpl
import com.github.danilodequeiroz.pinterestdl.presentation.dto.PinterestControllerImpl
import com.github.danilodequeiroz.pinterestdl.domain.PinterestMedia
import com.github.danilodequeiroz.pinterestdl.presentation.validation.PinterestUrlValidatorImpl
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job

val httpClient = HttpClient(CIO) {
    engine {
        requestTimeout = 10_000
    }
}

fun Application.configureRouting() {

    routing {
        get("/") {
            call.respondText("Hello World!")
        }

        get("/olddownload") {
            val pinterestUrl = call.request.queryParameters["url"]

            if (pinterestUrl.isNullOrEmpty()) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    PinterestMedia("", "", false, "URL query parameter is missing or empty.")
                )
                return@get
            }

            val pinterstScraper = PinterestControllerImpl(
                httpClient = httpClient,
                url = pinterestUrl,
            )
            val mediaLink = pinterstScraper.getMediaLink()

            if (mediaLink.success) {
                call.respond(HttpStatusCode.OK, mediaLink)
            } else {
                call.respond(HttpStatusCode.BadRequest, mediaLink)
            }
        }
        get("/download") {
            val pinterestUrl = call.request.queryParameters["url"] ?: EMPTY_STRING
            val logToObservability = LogToObservabilityImpl()

            val coroutineScope = CoroutineScope(Dispatchers.Main + Job())
            val pinterestHtmlParser = PinterestHtmlParserImpl(logToObservability = logToObservability)
            val ktorHttpClient1 = PinterestKtorHttpClientDataSourceImpl(
                httpClient = httpClient,
                coroutineScope = coroutineScope,
            )
            val pinterestHttpScrapingRepositoryImp = PinterestHttpScrapingRepositoryImp(
                pinterestUrlValidator = PinterestUrlValidatorImpl(),
                ktorHttpClient = ktorHttpClient1,
                pinterestHtmlParser = pinterestHtmlParser,
                logToObservability = logToObservability
            )

            val pinterestMedia = pinterestHttpScrapingRepositoryImp.getPinterestMedia(
                url = pinterestUrl
            )
            when(pinterestMedia){
                is RepositoryResult.Success -> call.respond(HttpStatusCode.OK, pinterestMedia.data)
                is RepositoryResult.Failure -> call.respond(HttpStatusCode.OK, PinterestMedia(message = "${pinterestMedia.genericMsg}  :  ${pinterestMedia.repositoryError}" , success = false, type = EMPTY_STRING, link = EMPTY_STRING))
            }
        }
    }
}

