package com.github.danilodequeiroz.pinterestdl.data.parser

import com.github.danilodequeiroz.pinterestdl.LogToObservability
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject

class PinterestHtmlParserImpl(val logToObservability: LogToObservability) : PinterestHtmlParser {

    override fun checkContentType(rawHtmlContent: String?) : HtmlParseValidationResult{
        if(rawHtmlContent.isNullOrEmpty()){
            val message = "html content cannot be null neither empty"
            return HtmlParseValidationResult.Failure(
                type = IMAGE,
                link = EMPTY_STRING,
                success = false,
                message = message,
                errors = listOf(message)
            )
        }
        val htmlParseValidationResult = try {
            if (isVideo(rawHtmlContent)) {
                validateRawHtmlContentForVideo(rawHtmlContent)
            } else {
                validateRawHtmlContentForImage(rawHtmlContent)
            }
        } catch (e: Exception) {
            logToObservability.logSuccess(
                value = "Scraping or WebPage JSON parsing error: ${e.message}"
            )
            val message = "An error occurred during data extraction: ${e.message}"
            HtmlParseValidationResult.Failure(
                type = IMAGE,
                link = EMPTY_STRING,
                success = false,
                message = message,
                errors = listOf(message)
            )
        }
        return htmlParseValidationResult
    }

    private fun isVideo(content: String): Boolean {
        return content.contains(VIDEO_SNIPPET)
    }

    private fun validateRawHtmlContentForImage(rawHtmlContent: String): HtmlParseValidationResult {
        val match = Regex(IMAGE_SNIPPET_JSON_LD)
            .find(rawHtmlContent)?.value

        return if (match != null) {
            val jsonStr = match
                .replace(Regex(IMAGE_SNIPPET_JSON_LD_OPENING_TAG), EMPTY_STRING)
                .replace(Regex(IMAGE_SNIPPET_JSON_LD_CLOSING_TAG), EMPTY_STRING)

            val jsonObject = Json.parseToJsonElement(jsonStr).jsonObject
            val imageUrl = jsonObject[IMAGE]?.toString()?.trim(QUOTE_MARK) ?: EMPTY_STRING

            if (imageUrl.isNotEmpty()) {
                HtmlParseValidationResult.Success(
                    type = IMAGE,
                    link = imageUrl,
                    success = true,
                    message = "Image link successfully found."
                )
            } else {
                val message = "Image link found but image field is empty."
                HtmlParseValidationResult.Failure(
                    type = IMAGE,
                    link = EMPTY_STRING,
                    success = false,
                    message = message,
                    errors = listOf(message)
                )
            }
        } else {
            val message = "Image metadata not found on the page content."
            HtmlParseValidationResult.Failure(
                type = IMAGE,
                link = EMPTY_STRING,
                success = false,
                message = message,
                errors = listOf(message)
            )
        }
    }

    private fun validateRawHtmlContentForVideo(rawHtmlContent: String): HtmlParseValidationResult {
        val match = Regex(VIDEO_SNIPPET_JSON_LD)
            .find(rawHtmlContent)?.value

        return if (match != null) {
            val jsonStr = match
                .replace(Regex(VIDEO_SNIPPET_JSON_LD_OPENING_TAG), EMPTY_STRING)
                .replace(Regex(VIDEO_SNIPPET_JSON_LD_CLOSING_TAG), EMPTY_STRING)

            val jsonObject = Json.parseToJsonElement(jsonStr).jsonObject
            val contentUrl = jsonObject[CONTENT_URL]?.toString()?.trim(QUOTE_MARK) ?: EMPTY_STRING

            if (contentUrl.isNotEmpty()) {
                HtmlParseValidationResult.Success(
                    type = VIDEO,
                    link = contentUrl,
                    success = true,
                    message = "Video link successfully found."
                )
            } else {
                val message = "Video link found but contentUrl field is empty."
                HtmlParseValidationResult.Failure(
                    type = VIDEO,
                    link = EMPTY_STRING,
                    success = false,
                    message = message,
                    errors = listOf(message)
                )
            }
        } else {
            val message = "Video metadata not found on the page content."
            HtmlParseValidationResult.Failure(
                type = VIDEO,
                link = EMPTY_STRING,
                success = false,
                message = message,
                errors = listOf(message)
            )
        }
    }
}