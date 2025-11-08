package com.github.danilodequeiroz.pinterestdl.domain.usecase

import com.github.danilodequeiroz.pinterestdl.domain.PinterestMedia
import com.github.danilodequeiroz.pinterestdl.domain.repository.PinterestHttpScrapingRepository

class FetchPinterestWebPageUseCaseImp(val pinterestHttpScrapingRepository: PinterestHttpScrapingRepository): FetchHtmlPageUseCase {
    override fun execute(url: String): PinterestMedia {


//        val mediaLink = pinterestHttpScrapingRepository.getPinterestMedia(
//            url = url
//        )
//        return mediaLink
        return PinterestMedia(
            type = "",
            link = "",
            success = true,
            message = "",
        )
    }
}