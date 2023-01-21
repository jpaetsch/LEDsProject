package com.example.ledscontroller.network

import android.util.Log
import com.example.ledscontroller.models.patterns.SolidPatternModel
import com.example.ledscontroller.models.ResponseModel
import com.example.ledscontroller.models.StatusModel
import com.example.ledscontroller.utils.LEDsReceiver
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.http.*

class ApiServiceImpl(private val client: HttpClient) : ApiService {
    override suspend fun getStatus(): StatusModel? {
        val apiRoute = (LEDsReceiver.ip ?: "").plus(ApiRoutes.GET_STATUS)
        return try {
            // 2xx response - parse
            client.request(apiRoute) {
                method = HttpMethod.Get
            }.body()
        } catch (ex: RedirectResponseException) {
            // 3xx response - error
            Log.e(apiRoute, ex.response.status.description)
            null
        } catch (ex: ClientRequestException) {
            // 4xx response - error
            Log.e(apiRoute, ex.response.status.description)
            null
        } catch (ex: ServerResponseException) {
            // 5xx response - error
            Log.e(apiRoute, ex.response.status.description)
            null
        }
    }

    override suspend fun turnOff(): ResponseModel? {
        val apiRoute = (LEDsReceiver.ip ?: "").plus(ApiRoutes.TURN_OFF)
        return try {
            // 2xx response - parse
            client.request(apiRoute) {
                method = HttpMethod.Post
            }.body()
        } catch (ex: RedirectResponseException) {
            // 3xx response - error
            Log.e(apiRoute, ex.response.status.description)
            null
        } catch (ex: ClientRequestException) {
            // 4xx response - error
            Log.e(apiRoute, ex.response.status.description)
            null
        } catch (ex: ServerResponseException) {
            // 5xx response - error
            Log.e(apiRoute, ex.response.status.description)
            null
        }
    }

    override suspend fun setSolidPattern(pattern: SolidPatternModel): ResponseModel? {
        val apiRoute = (LEDsReceiver.ip ?: "").plus(ApiRoutes.SET_PATTERN)
        return try {
            // 2xx response - parse
            client.request(apiRoute) {
                method = HttpMethod.Post
                setBody(pattern)
            }.body()
        } catch (ex: RedirectResponseException) {
            // 3xx response - error
            Log.e(apiRoute, ex.response.status.description)
            null
        } catch (ex: ClientRequestException) {
            // 4xx response - error
            Log.e(apiRoute, ex.response.status.description)
            null
        } catch (ex: ServerResponseException) {
            // 5xx response - error
            Log.e(apiRoute, ex.response.status.description)
            null
        }
    }
}