package com.github.danilodequeiroz.pinterestdl.data.repository.source.remote

import com.github.danilodequeiroz.pinterestdl.data.exception.AuthenticationFailedException
import com.github.danilodequeiroz.pinterestdl.data.exception.NetworkTimeoutException
import com.github.danilodequeiroz.pinterestdl.data.exception.RemoteServiceUnavailableException
import com.github.danilodequeiroz.pinterestdl.data.exception.ResourceNotFoundException
import com.github.danilodequeiroz.pinterestdl.data.repository.USER_AGENT_NAME
import com.github.danilodequeiroz.pinterestdl.data.repository.USER_AGENT_VALUE
import io.ktor.client.HttpClient
import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpStatusCode
import io.ktor.http.headers

class PinterestKtorHttpClientDataSourceImpl(
    val httpClient: HttpClient,
) : PinterestKtorHttpClientDataSource {
    override suspend fun getRawContent(cleanUrl: String): HttpResponse {
        try {
            val response = httpClient.get(urlString = cleanUrl) {
                headers {
                    headers.append(
                        name = USER_AGENT_NAME,
                        value = USER_AGENT_VALUE
                    )
                }
            }

            return when (response.status) {
                HttpStatusCode.NotFound -> throw ResourceNotFoundException(url = cleanUrl)
                HttpStatusCode.Unauthorized -> throw AuthenticationFailedException(url = cleanUrl)
                in HttpStatusCode.InternalServerError..HttpStatusCode.InsufficientStorage ->
                    throw RemoteServiceUnavailableException(url = cleanUrl, statusCode = response.status.value)

                else -> response
            }
        } catch (e: ConnectTimeoutException) {
            throw NetworkTimeoutException(url = cleanUrl, cause = e)
        } catch (e: ClientRequestException) {
            // Catches Ktor client error for 4xx status codes not handled above
            throw RemoteServiceUnavailableException(url = cleanUrl, statusCode = e.response.status.value, e.message)
        }
    }
}