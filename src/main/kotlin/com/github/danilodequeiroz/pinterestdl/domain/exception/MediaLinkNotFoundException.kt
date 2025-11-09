package com.github.danilodequeiroz.pinterestdl.domain.exception

class MediaLinkNotFoundException(
    message: String = "The requested media link could not be found or extracted."
) : BusinessRuleException(message = message)