package com.github.danilodequeiroz.pinterestdl.data.exception

class ContentParsingException(
    url: String, 
    detail: String
) : RemoteApiException("Failed to parse content from URL: $url. Details: $detail")
