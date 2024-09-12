package com.ronaldo.pace.network

import com.ronaldo.pace.network.mock.mockEngine
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.ANDROID
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

object ClientHttpFactory {
    fun create(): HttpClient {
        return HttpClient(mockEngine) {
            expectSuccess = true // throws exception on 4xx/5xx responses
            install(Logging) {
                logger = io.ktor.client.plugins.logging.Logger.ANDROID
                level = LogLevel.INFO
            }
            install(ContentNegotiation) {
                json(Json {
                    prettyPrint = true
                    ignoreUnknownKeys = true
                })
            }
        }
    }
}
