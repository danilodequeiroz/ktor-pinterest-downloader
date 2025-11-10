package com.github.danilodequeiroz.pinterestdl.presentation.server

import com.github.danilodequeiroz.pinterestdl.LogToObservability
import com.github.danilodequeiroz.pinterestdl.LogToObservabilityImpl
import com.github.danilodequeiroz.pinterestdl.data.parser.EMPTY_STRING
import com.github.danilodequeiroz.pinterestdl.data.parser.PinterestHtmlParserImpl
import com.github.danilodequeiroz.pinterestdl.data.repository.PinterestHttpScrapingRepositoryImp
import com.github.danilodequeiroz.pinterestdl.data.repository.source.remote.PinterestKtorHttpClientDataSourceImpl
import com.github.danilodequeiroz.pinterestdl.domain.PinterestMedia
import com.github.danilodequeiroz.pinterestdl.domain.exception.BusinessRuleException
import com.github.danilodequeiroz.pinterestdl.domain.exception.ExternalServiceUnreachableException
import com.github.danilodequeiroz.pinterestdl.domain.exception.InvalidMediaContentException
import com.github.danilodequeiroz.pinterestdl.domain.exception.MediaLinkNotFoundException
import com.github.danilodequeiroz.pinterestdl.domain.repository.PinterestHttpScrapingRepository
import com.github.danilodequeiroz.pinterestdl.domain.usecase.FetchPinterestWebPageUseCase
import com.github.danilodequeiroz.pinterestdl.domain.usecase.FetchPinterestWebPageUseCaseImpl
import com.github.danilodequeiroz.pinterestdl.presentation.dto.ErrorResponse
import com.github.danilodequeiroz.pinterestdl.presentation.validation.PinterestUrlValidatorImpl
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
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
    install(StatusPages) {
        exception<MediaLinkNotFoundException> { call, cause ->
            val response = ErrorResponse(
                status = HttpStatusCode.NotFound.value,
                message = cause.message ?: "The requested media resource does not exist."
            )
            call.respond(HttpStatusCode.NotFound, response)
        }

        exception<InvalidMediaContentException> { call, cause ->
            val response = ErrorResponse(
                status = HttpStatusCode.BadRequest.value,
                message = cause.message ?: "The content structure is invalid for scraping."
            )
            call.respond(HttpStatusCode.BadRequest, response)
        }

        exception<BusinessRuleException> { call, cause ->
            val response = ErrorResponse(
                status = HttpStatusCode.Forbidden.value,
                message = cause.message ?: "Access to perform this action is forbidden."
            )
            call.respond(HttpStatusCode.Forbidden, response)
        }

        exception<ExternalServiceUnreachableException> { call, cause ->
            val response = ErrorResponse(
                status = HttpStatusCode.ServiceUnavailable.value,
                message = "The external service is currently unreachable. Please try again later. ccase: $cause"
            )
            call.respond(HttpStatusCode.ServiceUnavailable, response)
        }

        exception<Throwable> { call, cause ->
            call.application.environment.log.error("Unhandled Exception:", cause)

            val response = ErrorResponse(
                status = HttpStatusCode.InternalServerError.value,
                message = "An internal server error occurred."
            )
            call.respond(HttpStatusCode.InternalServerError, response)
        }
    }
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

            when (val pinterestMedia = useCase.execute(url = pinterestUrl)) {
                is PinterestMedia -> call.respond(HttpStatusCode.OK, pinterestMedia)
            }
        }
    }
}


