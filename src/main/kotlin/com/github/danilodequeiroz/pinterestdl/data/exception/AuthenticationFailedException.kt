package com.github.danilodequeiroz.pinterestdl.data.exception

class AuthenticationFailedException(
    url: String,
    message: String = "The requested resource is unauthorized because the request lacks valid authentication credentials: $url"
) : RemoteApiException(message, statusCode = 401)