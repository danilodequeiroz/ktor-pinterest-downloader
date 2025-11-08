package com.github.danilodequeiroz.pinterestdl.domain.usecase

import com.github.danilodequeiroz.pinterestdl.domain.PinterestMedia

interface FetchHtmlPageUseCase {

    fun execute(url :String): PinterestMedia
}