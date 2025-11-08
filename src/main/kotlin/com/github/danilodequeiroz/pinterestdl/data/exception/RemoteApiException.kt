package com.github.danilodequeiroz.pinterestdl.data.exception

abstract class RemoteApiException(
    message: String, 
    val statusCode: Int? = null,
    cause: Throwable? = null
) : RuntimeException(message, cause)