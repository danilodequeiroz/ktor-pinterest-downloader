package com.github.danilodequeiroz.pinterestdl.presentation.validation

import java.util.regex.Pattern

class PinterestUrlValidatorImpl : PinterestUrlValidator {
    /** Verifica se a URL fornecida é um formato válido de pin do Pinterest. */
    override fun isValidUrl(url : String): Boolean {
        val pattern = Pattern.compile("(^http(s)?://)?(www.)?pinterest.\\w+/pin/[\\w\\-?]+")
        return pattern.matcher(url).matches()
    }
}