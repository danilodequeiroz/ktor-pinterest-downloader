package com.github.danilodequeiroz.pinterestdl.presentation.server

import com.github.danilodequeiroz.pinterestdl.domain.exception.BusinessRuleException
import com.github.danilodequeiroz.pinterestdl.domain.exception.ExternalServiceUnreachableException
import com.github.danilodequeiroz.pinterestdl.domain.exception.InvalidMediaContentException
import com.github.danilodequeiroz.pinterestdl.domain.exception.MediaLinkNotFoundException
import com.github.danilodequeiroz.pinterestdl.presentation.dto.ErrorResponse
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.respond


fun Application.configureStatusPages() {
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
}