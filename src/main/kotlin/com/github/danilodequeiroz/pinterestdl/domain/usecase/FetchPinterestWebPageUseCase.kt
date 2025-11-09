package com.github.danilodequeiroz.pinterestdl.domain.usecase

import com.github.danilodequeiroz.pinterestdl.domain.PinterestMedia

interface FetchPinterestWebPageUseCase {

    suspend fun execute(url :String): PinterestMedia
}