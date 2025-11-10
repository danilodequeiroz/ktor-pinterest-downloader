package com.github.danilodequeiroz.pinterestdl.presentation.server

import com.github.danilodequeiroz.pinterestdl.LogToObservability
import com.github.danilodequeiroz.pinterestdl.LogToObservabilityImpl
import com.github.danilodequeiroz.pinterestdl.data.parser.EMPTY_STRING
import com.github.danilodequeiroz.pinterestdl.data.parser.PinterestHtmlParserImpl
import com.github.danilodequeiroz.pinterestdl.data.repository.PinterestHttpScrapingRepositoryImp
import com.github.danilodequeiroz.pinterestdl.data.repository.source.remote.PinterestKtorHttpClientDataSourceImpl
import com.github.danilodequeiroz.pinterestdl.domain.PinterestMedia
import com.github.danilodequeiroz.pinterestdl.domain.repository.PinterestHttpScrapingRepository
import com.github.danilodequeiroz.pinterestdl.domain.usecase.FetchPinterestWebPageUseCase
import com.github.danilodequeiroz.pinterestdl.domain.usecase.FetchPinterestWebPageUseCaseImpl
import com.github.danilodequeiroz.pinterestdl.presentation.dto.ErrorResponse
import com.github.danilodequeiroz.pinterestdl.presentation.validation.PinterestUrlValidator
import com.github.danilodequeiroz.pinterestdl.presentation.validation.PinterestUrlValidatorImpl
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.respond
import io.ktor.server.routing.*

val httpClient = HttpClient(CIO) {
    engine {
        requestTimeout = 10_000
    }
    install(Logging) {
        level = LogLevel.ALL

        logger = Logger.DEFAULT

        filter { request ->
            !request.url.encodedPath.contains("/health")
        }

        sanitizeHeader { header ->
            header == "Authorization"
        }
    }
}

private fun createLogToObservability(): LogToObservability {
    return LogToObservabilityImpl()
}

private fun createValidator(): PinterestUrlValidator {
    return PinterestUrlValidatorImpl()
}

private fun createPinterestHttpScrapingRepository(
    logToObservability: LogToObservability,
): PinterestHttpScrapingRepository {
    val pinterestHtmlParser = PinterestHtmlParserImpl(logToObservability = logToObservability)
    val ktorHttpClient = PinterestKtorHttpClientDataSourceImpl(
        httpClient = httpClient,
    )

    return PinterestHttpScrapingRepositoryImp(
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
        get("/download") {
            val pinterestUrl = call.request.queryParameters["url"] ?: EMPTY_STRING
            val logToObservability = createLogToObservability()
            val pinterestHttpScrapingRepository = createPinterestHttpScrapingRepository(
                logToObservability = logToObservability,
            )

            val useCase: FetchPinterestWebPageUseCase = createFetchPinterestWebPageUseCase(
                pinterestHttpScrapingRepository = pinterestHttpScrapingRepository
            )

            val pinterestUrlValidator = createValidator()

            if (pinterestUrl == EMPTY_STRING) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    ErrorResponse(HttpStatusCode.BadRequest.value, "URL query parameter is missing.")
                )
                return@get
            }

            if (!pinterestUrlValidator.isValidUrl(pinterestUrl)) {
                val errorResponse = ErrorResponse(
                    status = HttpStatusCode.BadRequest.value,
                    message = "Invalid Pinterest URL format."
                )
                call.respond(HttpStatusCode.BadRequest, errorResponse)
                return@get
            }

            when (val pinterestMedia = useCase.execute(url = pinterestUrl)) {
                is PinterestMedia -> call.respond(HttpStatusCode.OK, pinterestMedia)
            }
        }
    }
}


