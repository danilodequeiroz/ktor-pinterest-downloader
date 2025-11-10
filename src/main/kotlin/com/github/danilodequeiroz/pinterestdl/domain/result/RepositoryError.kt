package com.github.danilodequeiroz.pinterestdl.domain.result

sealed class RepositoryError {
    data object UserUrlError : RepositoryError()
    data object HtmlParseError : RepositoryError()
    data object NotFound : RepositoryError()
    data object Unauthorized : RepositoryError()
    data object InternalServerError : RepositoryError()

    data object HttpClientNetworkError : RepositoryError()

    data object DataParsingError : RepositoryError()

    data class UnknownError(val unknownErrorMsg: String) : RepositoryError()
}