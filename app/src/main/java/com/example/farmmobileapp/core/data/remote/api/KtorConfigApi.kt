package com.example.farmmobileapp.core.data.remote.api

import android.util.Log
import com.example.farmmobileapp.BuildConfig
import com.example.farmmobileapp.core.data.preferences.model.RetrieveResponse
import com.example.farmmobileapp.core.storage.TokenRepository
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.http.isSuccess
import javax.inject.Inject

class KtorConfigApi @Inject constructor(
    private val httpClient: HttpClient,
//    private val tokenRepository: TokenRepository
) : ConfigApi {
    private val baseUrl = BuildConfig.BASE_URL

    override suspend fun getMapboxAccessToken(): RetrieveResponse {
        return try {
//            val token = tokenRepository.getAccessToken() ?: throw Exception("No token available")
            val response = httpClient.get("$baseUrl/config/retrieve/mapbox-access-token") {
//                header("Authorization", "Bearer $token")
            }
            if (response.status.isSuccess()) {
                Log.d("KtorConfigApi", "Response: ${response.status}")
                response.body<RetrieveResponse>()
            } else {
                Log.e("KtorConfigApi", "Error: ${response.status.description}")
                throw Exception("Failed to fetch Mapbox access token: ${response.status.description}")
            }
        } catch (e: Exception) {
            Log.e("KtorConfigApi", "Exception: ${e.message}")
            throw Exception("Failed to fetch Mapbox access token: ${e.message}")
        }
    }
}