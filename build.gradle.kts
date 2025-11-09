plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.ktor)
    alias(libs.plugins.org.jetbrains.kotlin.plugin.serialization)
}

group = "com.github.danilodequeiroz"
version = "0.0.1"

application {
    mainClass = "io.ktor.server.netty.EngineMain"
}

dependencies {
    implementation(libs.ktor.server.core)
    implementation(libs.logback.classic)
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.config.yaml)

    /** coroutines server engine **/
    implementation(libs.ktor.server.cio)
    implementation(libs.ktor.server.content.negotiation)
    /** client content negotiation **/
    implementation(libs.ktor.client.cio)
    implementation(libs.ktor.client.core)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.jdk8)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.ktor.client.content.negotiation)

    testImplementation(libs.ktor.server.test.host)
    testImplementation(libs.kotlin.test.junit)
}
