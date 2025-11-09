package com.github.danilodequeiroz.pinterestdl.domain.exception

class InvalidMediaContentException(
    message: String = "The content was found but is invalid according to business rules."
) : BusinessRuleException(message = message)