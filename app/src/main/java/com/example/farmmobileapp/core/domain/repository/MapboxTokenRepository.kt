package com.example.farmmobileapp.core.domain.repository

interface MapboxTokenRepository {
    suspend fun getToken(): String
    suspend fun saveToken(token: String)
}