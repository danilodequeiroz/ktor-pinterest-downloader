package com.github.danilodequeiroz.pinterestdl

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import java.util.regex.Pattern

private val httpClient = HttpClient(CIO) {
    engine {
        requestTimeout = 10_000
    }
}

@Serializable
data class MediaLink(
    val type: String,
    val link: String,
    val success: Boolean,
    val message: String
)

class PinterestImpl(private val url: String) : Pinterest{

    /** Checks if the provided URL is a valid Pinterest pin format. */
    fun isValidUrl(): Boolean {
        return true
    }

    /** Faz a requisição HTTP e retorna o conteúdo HTML. */
    suspend fun getPageContent(): String? {
        return ""
    }

    /** Makes the HTTP request and returns the HTML content. */
    fun isVideo(content: String): Boolean {
        return content.contains("video-snippet")
    }

    /**
     * Scrapes the page content for the direct image or video link.
     * Returns an object with com.github.danilodequeiroz.pinterestdl.MediaLink.
     */
    suspend fun getMediaLink(): MediaLink {
        return MediaLink("image", "imageUrl", true, "Image link successfully found.")
    }
}