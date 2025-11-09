package com.github.danilodequeiroz.pinterestdl.presentation.dto

import kotlinx.serialization.Serializable

@Serializable
data class ErrorResponse(
    val status: Int,
    val message: String,
    val code: String? = null
)