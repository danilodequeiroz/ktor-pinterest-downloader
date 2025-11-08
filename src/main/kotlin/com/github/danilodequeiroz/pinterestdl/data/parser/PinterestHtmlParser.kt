package com.github.danilodequeiroz.pinterestdl.data.parser

interface PinterestHtmlParser {
    fun checkContentType(rawHtmlContent : String?) : HtmlParseValidationResult
}