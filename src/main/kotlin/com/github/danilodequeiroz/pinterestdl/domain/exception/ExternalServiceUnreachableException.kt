package com.github.danilodequeiroz.pinterestdl.domain.exception

class ExternalServiceUnreachableException(
    message: String = "External service is currently unavailable or timed out."
) : BusinessRuleException(message = message)