package com.github.danilodequeiroz.pinterestdl.domain.result

sealed class RepositoryResult<out T> {
    data class Success<T>(val data: T) : RepositoryResult<T>()
    data class Failure(val genericMsg: String? = null, val repositoryError: RepositoryError) : RepositoryResult<Nothing>()
}