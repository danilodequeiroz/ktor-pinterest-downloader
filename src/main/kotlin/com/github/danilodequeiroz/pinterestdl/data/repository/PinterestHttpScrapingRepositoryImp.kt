package com.github.danilodequeiroz.pinterestdl.data.repository

import com.github.danilodequeiroz.pinterestdl.LogToObservability
import com.github.danilodequeiroz.pinterestdl.data.parser.PinterestHtmlParser
import com.github.danilodequeiroz.pinterestdl.domain.PinterestMedia
import com.github.danilodequeiroz.pinterestdl.domain.repository.PinterestHttpScrapingRepository
import com.github.danilodequeiroz.pinterestdl.data.parser.HtmlParseValidationResult
import com.github.danilodequeiroz.pinterestdl.data.repository.source.remote.PinterestKtorHttpClientDataSource
import com.github.danilodequeiroz.pinterestdl.presentation.validation.PinterestUrlValidator
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess

const val USER_AGENT_NAME = "User-Agent"
const val USER_AGENT_VALUE = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, como Gecko) Chrome/58.0.3029.110 Safari/537.3"

class PinterestHttpScrapingRepositoryImp(
    val pinterestUrlValidator: PinterestUrlValidator,
    val ktorHttpClient : PinterestKtorHttpClientDataSource,
    val pinterestHtmlParser: PinterestHtmlParser,
    val logToObservability: LogToObservability,
) : PinterestHttpScrapingRepository {
    override suspend fun getPinterestMedia(url: String): RepositoryResult<PinterestMedia> {
        var message :String? = null
        if (!pinterestUrlValidator.isValidUrl(url = url)) {
            message = "Invalid Pinterest URL format."
            logToObservability.logWarning(
                value = message
            )
            return RepositoryResult.Failure(
                genericMsg = message,
                repositoryError = RepositoryError.UserUrlError
            )
        }
        val rawHtml = try {
            val response = ktorHttpClient.regularGet(
                cleanUrl = url
            )
            response.bodyAsText().takeIf { response.status.isSuccess() } ?: run {
                message = "Request failed because pinterest html response body is null"
                null
            }
        } catch (e: Exception) {
            message = "Request failed for $url: ${e.message}"
            null
        }
        message?.let {
            logToObservability.logError(
                value = message
            )
        }
        return when (val validationResult = pinterestHtmlParser.checkContentType(rawHtmlContent = rawHtml)) {
            is HtmlParseValidationResult.Failure -> {
                logToObservability.logError(
                    value = validationResult.message,
                )
                RepositoryResult.Failure(
                    genericMsg = validationResult.message,
                    repositoryError = RepositoryError.HtmlParseError
                )
            }
            is HtmlParseValidationResult.Success -> {
                RepositoryResult.Success(
                    data = PinterestMedia(
                        type = validationResult.type,
                        link = validationResult.link,
                        success = validationResult.success,
                        message = validationResult.message
                    )
                )
            }
        }
    }
}