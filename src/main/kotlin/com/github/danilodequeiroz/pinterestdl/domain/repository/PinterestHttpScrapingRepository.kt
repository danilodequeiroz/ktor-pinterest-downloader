package com.github.danilodequeiroz.pinterestdl.domain.repository

import com.github.danilodequeiroz.pinterestdl.domain.result.RepositoryResult
import com.github.danilodequeiroz.pinterestdl.domain.PinterestMedia


interface PinterestHttpScrapingRepository {

    suspend fun getPinterestMedia(url : String) : RepositoryResult<PinterestMedia>

}