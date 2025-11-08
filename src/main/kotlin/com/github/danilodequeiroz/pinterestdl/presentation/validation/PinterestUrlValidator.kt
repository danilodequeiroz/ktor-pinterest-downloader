package com.github.danilodequeiroz.pinterestdl.presentation.validation

interface PinterestUrlValidator {
    fun isValidUrl(url : String): Boolean
}