package com.github.danilodequeiroz.pinterestdl.data.parser

internal const val VIDEO_SNIPPET_JSON_LD_CLOSING_TAG = "</script>"
internal const val VIDEO_SNIPPET_JSON_LD = "<script data-test-id=\"video-snippet\".+?</script>"
internal const val VIDEO_SNIPPET_JSON_LD_OPENING_TAG =
    "<script data-test-id=\"video-snippet\" type=\"application/ld\\+json\">"

internal const val IMAGE_SNIPPET_JSON_LD = "<script data-test-id=\"leaf-snippet\".+?</script>"
internal const val IMAGE_SNIPPET_JSON_LD_OPENING_TAG =
    "<script data-test-id=\"leaf-snippet\" type=\"application/ld\\+json\">"
internal const val IMAGE_SNIPPET_JSON_LD_CLOSING_TAG = "</script>"

internal const val EMPTY_STRING = ""
internal const val QUOTE_MARK = '"'

internal const val VIDEO_SNIPPET = "video-snippet"
internal const val IMAGE = "image"
internal const val VIDEO = "video"
internal const val CONTENT_URL = "contentUrl"