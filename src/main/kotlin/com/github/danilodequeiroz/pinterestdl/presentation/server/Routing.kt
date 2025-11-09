package com.github.danilodequeiroz.pinterestdl.presentation.server

import com.github.danilodequeiroz.pinterestdl.LogToObservability
import com.github.danilodequeiroz.pinterestdl.LogToObservabilityImpl
import com.github.danilodequeiroz.pinterestdl.data.parser.EMPTY_STRING
import com.github.danilodequeiroz.pinterestdl.data.parser.PinterestHtmlParserImpl
import com.github.danilodequeiroz.pinterestdl.data.repository.PinterestHttpScrapingRepositoryImp
import com.github.danilodequeiroz.pinterestdl.data.repository.source.remote.PinterestKtorHttpClientDataSourceImpl
import com.github.danilodequeiroz.pinterestdl.presentation.dto.PinterestControllerImpl
import com.github.danilodequeiroz.pinterestdl.domain.PinterestMedia
import com.github.danilodequeiroz.pinterestdl.domain.repository.PinterestHttpScrapingRepository
import com.github.danilodequeiroz.pinterestdl.domain.usecase.FetchPinterestWebPageUseCase
import com.github.danilodequeiroz.pinterestdl.domain.usecase.FetchPinterestWebPageUseCaseImpl
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

private fun createLogToObservability(): LogToObservability {
    return LogToObservabilityImpl()
}

private fun createCoroutineScope(): CoroutineScope {
    return CoroutineScope(Dispatchers.Main + Job())
}

private fun createPinterestHttpScrapingRepository(
    logToObservability: LogToObservability,
): PinterestHttpScrapingRepository {
    val pinterestHtmlParser = PinterestHtmlParserImpl(logToObservability = logToObservability)
    val ktorHttpClient = PinterestKtorHttpClientDataSourceImpl(
        httpClient = httpClient,
    )

    return PinterestHttpScrapingRepositoryImp(
        pinterestUrlValidator = PinterestUrlValidatorImpl(),
        ktorHttpClient = ktorHttpClient,
        pinterestHtmlParser = pinterestHtmlParser,
        logToObservability = logToObservability
    )
}

private fun createFetchPinterestWebPageUseCase(
    pinterestHttpScrapingRepository: PinterestHttpScrapingRepository
): FetchPinterestWebPageUseCase {
    return FetchPinterestWebPageUseCaseImpl(
        pinterestHttpScrapingRepository = pinterestHttpScrapingRepository
    )
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

            val pinterestControllerImpl = PinterestControllerImpl(
                httpClient = httpClient,
                url = pinterestUrl,
            )
            val mediaLink = pinterestControllerImpl.getMediaLink()

            if (mediaLink.success) {
                call.respond(HttpStatusCode.OK, mediaLink)
            } else {
                call.respond(HttpStatusCode.BadRequest, mediaLink)
            }
        }

        get("/download") {
            val pinterestUrl = call.request.queryParameters["url"] ?: EMPTY_STRING
            val logToObservability = createLogToObservability()
            val pinterestHttpScrapingRepository = createPinterestHttpScrapingRepository(
                logToObservability = logToObservability,
            )

            val useCase: FetchPinterestWebPageUseCase = createFetchPinterestWebPageUseCase(
                pinterestHttpScrapingRepository = pinterestHttpScrapingRepository
            )

            when (val pinterestMedia = useCase.execute(url = pinterestUrl)) {
                is PinterestMedia -> call.respond(HttpStatusCode.OK, pinterestMedia)
            }
        }
    }
}

