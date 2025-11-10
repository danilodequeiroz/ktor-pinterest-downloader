package com.github.danilodequeiroz.pinterestdl.data.repository.source.remote

import io.ktor.client.statement.HttpResponse

interface PinterestKtorHttpClientDataSource {
    suspend fun getRawContent(cleanUrl: String): HttpResponse
}
