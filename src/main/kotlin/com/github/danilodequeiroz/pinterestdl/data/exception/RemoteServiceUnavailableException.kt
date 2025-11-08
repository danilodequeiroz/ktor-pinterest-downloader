package com.github.danilodequeiroz.pinterestdl.data.exception

class RemoteServiceUnavailableException(
    url: String,
    statusCode: Int,
    message: String = "External service returned error code: $statusCode (url: $url)"
) : RemoteApiException(message, statusCode)