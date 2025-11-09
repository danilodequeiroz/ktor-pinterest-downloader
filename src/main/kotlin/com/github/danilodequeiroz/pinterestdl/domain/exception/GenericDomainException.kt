package com.github.danilodequeiroz.pinterestdl.domain.exception

class GenericDomainException(
    message: String = "An unexpected domain error occurred.",
    cause: Throwable? = null
) : BusinessRuleException(
    message = message,
    cause = cause
)