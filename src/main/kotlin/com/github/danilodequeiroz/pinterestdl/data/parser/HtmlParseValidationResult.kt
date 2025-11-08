package com.github.danilodequeiroz.pinterestdl.data.parser

sealed class HtmlParseValidationResult {
    data class Success(
        val type: String,
        val link: String,
        val success: Boolean,
        val message: String
    ) : HtmlParseValidationResult()
    data class Failure(
        val type: String,
        val link: String,
        val success: Boolean,
        val message: String,
        val errors: List<String>) : HtmlParseValidationResult()
}