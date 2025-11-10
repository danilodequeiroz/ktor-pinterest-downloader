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
const val USER_AGENT_VALUE = "Mozillla/5.0 (Windows NT 10.0; Win64; x64)"

class PinterestHttpScrapingRepositoryImp(
    val ktorHttpClient : PinterestKtorHttpClientDataSource,
    val pinterestHtmlParser: PinterestHtmlParser,
    val logToObservability: LogToObservability,
) : PinterestHttpScrapingRepository {
    override suspend fun getPinterestMedia(url: String): RepositoryResult<PinterestMedia> {
        var message :String? = null

        val rawHtml = try {
            val response = ktorHttpClient.getRawContent(
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