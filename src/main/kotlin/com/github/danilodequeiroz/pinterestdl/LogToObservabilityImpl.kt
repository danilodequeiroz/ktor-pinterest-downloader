package com.github.danilodequeiroz.pinterestdl

class LogToObservabilityImpl : LogToObservability {
    override fun logSuccess(value: String) {
        println(value)
    }

    override fun logError(value: String) {
        println(value)
    }

    override fun logWarning(value: String) {
        println(value)
    }
}