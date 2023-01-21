package com.example.ledscontroller.network

import com.example.ledscontroller.models.patterns.SolidPatternModel
import com.example.ledscontroller.models.ResponseModel
import com.example.ledscontroller.models.StatusModel
import io.ktor.client.*
import io.ktor.client.engine.android.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json


interface ApiService {

    suspend fun getStatus(): StatusModel?
    suspend fun turnOff(): ResponseModel?
    suspend fun setSolidPattern(pattern: SolidPatternModel): ResponseModel?

    companion object {
        fun create(): ApiService {
            return ApiServiceImpl(
                client = HttpClient(Android) {
                    expectSuccess = true
                    followRedirects = false

                    // KTOR Logging
                    install(Logging) {
                        level = LogLevel.ALL
                    }
                    // KTOR Json
                    install(ContentNegotiation) {
                        json(Json {
                            ignoreUnknownKeys = true
                        })
                    }
                    //Timeout
                    install(HttpTimeout) {
                        requestTimeoutMillis = 15000L
                        connectTimeoutMillis = 15000L
                        socketTimeoutMillis = 15000L
                    }
                    // Apply to all requests
                    defaultRequest {
                        // ...
                    }
                }
            )
        }
    }
}