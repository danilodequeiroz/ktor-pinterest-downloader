package com.github.danilodequeiroz.pinterestdl.presentation.dto

import com.github.danilodequeiroz.pinterestdl.domain.PinterestMedia
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import java.util.regex.Pattern

class PinterestControllerImpl(
    private val httpClient: HttpClient,
    private val url: String
) : Pinterest {

    /** Checks if the provided URL is a valid Pinterest pin format. */
    fun isValidUrl(): Boolean {
        val pattern = Pattern.compile("(^http(s)?://)?(www.)?pinterest.\\w+/pin/[\\w\\-?]+")
        return pattern.matcher(url).matches()
    }

    /** Makes an HTTP request and returns the HTML content. */
    suspend fun getPageContent(): String? {
        return try {
            val response = httpClient.get(url) {
                // Additional headers (ie: User-Agent)
            }
            response.bodyAsText().takeIf { response.status.isSuccess() }
        } catch (e: Exception) {
            println("Request failed for $url: ${e.message}")
            null
        }
    }

    /** Checks if the pin is a video based on the HTML content. */
    fun isVideo(content: String): Boolean {
        return content.contains("video-snippet")
    }

    /**
     * Scrapes the page content for the direct image or video link.
     * Returns an object of com.github.danilodequeiroz.pinterestdl.domain.MediaLink.
     */
    suspend fun getMediaLink(): PinterestMedia {
        if (!isValidUrl()) {
            return PinterestMedia("", "", false, "Invalid com.github.danilodequeiroz.pinterestdl.presentation.dto.Pinterest URL format.")
        }

        val content = getPageContent()
        if (content.isNullOrEmpty()) {
            return PinterestMedia("", "", false, "Failed to fetch page content or content is empty.")
        }

        return try {
            if (isVideo(content)) {
                // Regex to find the JSON-LD block for the video
                val match = Regex("<script data-test-id=\"video-snippet\".+?</script>")
                    .find(content)?.value

                if (match != null) {
                    val jsonStr = match
                        .replace(Regex("<script data-test-id=\"video-snippet\" type=\"application/ld\\+json\">"), "")
                        .replace(Regex("</script>"), "")

                    val jsonObject = Json.parseToJsonElement(jsonStr).jsonObject
                    val contentUrl = jsonObject["contentUrl"]?.toString()?.trim('"') ?: ""

                    if (contentUrl.isNotEmpty()) {
                        PinterestMedia("video", contentUrl, true, "Video link successfully found.")
                    } else {
                        PinterestMedia("video", "", false, "Video link found but contentUrl field is empty.")
                    }
                } else {
                    PinterestMedia("video", "", false, "Video metadata not found on the page.")
                }
            } else {
                val match = Regex("<script data-test-id=\"leaf-snippet\".+?</script>")
                    .find(content)?.value

                if (match != null) {
                    val jsonStr = match
                        .replace(Regex("<script data-test-id=\"leaf-snippet\" type=\"application/ld\\+json\">"), "")
                        .replace(Regex("</script>"), "")

                    val jsonObject = Json.parseToJsonElement(jsonStr).jsonObject
                    val imageUrl = jsonObject["image"]?.toString()?.trim('"') ?: ""

                    if (imageUrl.isNotEmpty()) {
                        PinterestMedia("image", imageUrl, true, "Image link successfully found.")
                    } else {
                        PinterestMedia("image", "", false, "Image link found but image field is empty.")
                    }
                } else {
                    PinterestMedia("image", "", false, "Image metadata not found on the page.")
                }
            }
        } catch (e: Exception) {
            println("Scraping or JSON parsing error: ${e.message}")
            PinterestMedia("", "", false, "An error occurred during data extraction: ${e.message}")
        }
    }
}