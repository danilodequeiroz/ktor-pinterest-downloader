package com.github.danilodequeiroz.pinterestdl.domain.usecase

import com.github.danilodequeiroz.pinterestdl.data.repository.RepositoryError
import com.github.danilodequeiroz.pinterestdl.data.repository.RepositoryResult
import com.github.danilodequeiroz.pinterestdl.domain.PinterestMedia
import com.github.danilodequeiroz.pinterestdl.domain.exception.ExternalServiceUnreachableException
import com.github.danilodequeiroz.pinterestdl.domain.exception.GenericDomainException
import com.github.danilodequeiroz.pinterestdl.domain.exception.InvalidMediaContentException
import com.github.danilodequeiroz.pinterestdl.domain.exception.MediaLinkNotFoundException
import com.github.danilodequeiroz.pinterestdl.domain.repository.PinterestHttpScrapingRepository

class FetchPinterestWebPageUseCaseImpl(
    val pinterestHttpScrapingRepository: PinterestHttpScrapingRepository,
) : FetchPinterestWebPageUseCase {
    override suspend fun execute(url: String): PinterestMedia {

        return when (val result = pinterestHttpScrapingRepository.getPinterestMedia(url = url)) {
            is RepositoryResult.Success -> {
                result.data
            }

            is RepositoryResult.Failure -> {
                when (result.repositoryError) {

                    is RepositoryError.NotFound -> {
                        throw MediaLinkNotFoundException("Pinterest media not found for URL: $url")
                    }

                    is RepositoryError.HttpClientNetworkError,
                    is RepositoryError.InternalServerError -> {
                        throw ExternalServiceUnreachableException(
                            "Pinterest service is unreachable or returned a server error."
                        )
                    }

                    is RepositoryError.HtmlParseError,
                    is RepositoryError.DataParsingError -> {
                        throw InvalidMediaContentException("Could not extract valid media link from content.")
                    }

                    else -> throw GenericDomainException("An unexpected business error occurred.")
                }
            }
        }
    }
}