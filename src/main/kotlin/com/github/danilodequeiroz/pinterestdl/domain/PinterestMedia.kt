package com.github.danilodequeiroz.pinterestdl.domain

import kotlinx.serialization.Serializable

@Serializable
data class PinterestMedia(
    val type: String,
    val link: String,
    val success: Boolean,
    val message: String
)