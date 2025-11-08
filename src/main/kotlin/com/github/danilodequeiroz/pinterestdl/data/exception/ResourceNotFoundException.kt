package com.github.danilodequeiroz.pinterestdl.data.exception

class ResourceNotFoundException(
    url: String,
    message: String = "The requested resource was not found at: $url"
) : RemoteApiException(message, statusCode = 404)