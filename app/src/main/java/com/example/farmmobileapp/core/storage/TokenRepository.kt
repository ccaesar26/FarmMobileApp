package com.example.farmmobileapp.core.storage

interface TokenRepository {
    suspend fun saveAccessToken(token: String)
    suspend fun getAccessToken(): String?
    suspend fun clearAccessToken()
    suspend fun saveRefreshToken(refreshToken: String)
    suspend fun getRefreshToken(): String?
    suspend fun clearRefreshToken()
}