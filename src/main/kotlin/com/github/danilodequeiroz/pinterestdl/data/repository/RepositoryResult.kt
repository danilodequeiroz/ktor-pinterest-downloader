package com.github.danilodequeiroz.pinterestdl.data.repository

sealed class RepositoryResult<out T> {
    data class Success<T>(val data: T) : RepositoryResult<T>()
    data class Failure(val genericMsg: String? = null, val repositoryError: RepositoryError) : RepositoryResult<Nothing>()
}

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