package com.github.danilodequeiroz.pinterestdl

interface LogToObservability {
    fun logSuccess(value:String)
    fun logError(value:String)
    fun logWarning(value:String)
}