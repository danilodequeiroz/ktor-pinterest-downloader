package com.github.danilodequeiroz.pinterestdl.data.exception

class NetworkTimeoutException(
    url: String, 
    cause: Throwable? = null
) : RemoteApiException("Network timeout when accessing resource: $url", cause = cause)
