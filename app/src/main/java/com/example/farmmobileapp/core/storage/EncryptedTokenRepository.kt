package com.example.farmmobileapp.core.storage

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import com.example.farmmobileapp.core.data.preferences.model.UserPreferences
import com.example.farmmobileapp.core.data.preferences.model.UserPreferencesSerializer
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking

private val Context.dataStore: DataStore<UserPreferences> by dataStore(
    fileName = "user-preferences",
    serializer = UserPreferencesSerializer
)

class EncryptedTokenRepository(private val context: Context) : TokenRepository {

    override suspend fun saveAccessToken(token: String) {
        // Use runBlocking to perform synchronous DataStore operation (for simplicity in TokenManager)
        runBlocking {
            context.dataStore.updateData { currentUserPreferences -> // Use updateData instead of edit
                currentUserPreferences.copy(accessToken = token) // Update token using copy method
            }
        }
    }

    override suspend fun getAccessToken(): String? {
        return runBlocking { // Use runBlocking for synchronous DataStore operation
            context.dataStore.data.map { userPreferences ->
                userPreferences.accessToken
            }.first()
        }
    }

    override suspend fun clearAccessToken() {
        runBlocking { // Use runBlocking for synchronous DataStore operation
            context.dataStore.updateData { currentUserPreferences -> // Use updateData instead of edit
                currentUserPreferences.copy(accessToken = null) // Clear token by setting to null
            }
        }
    }

    override suspend fun saveRefreshToken(refreshToken: String) {
        runBlocking { // Use runBlocking for synchronous DataStore operation
            context.dataStore.updateData { currentUserPreferences -> // Use updateData instead of edit
                currentUserPreferences.copy(refreshToken = refreshToken) // Update refresh token using copy method
            }
        }
    }

    override suspend fun getRefreshToken(): String? {
        return runBlocking { // Use runBlocking for synchronous DataStore operation
            context.dataStore.data.map { userPreferences ->
                userPreferences.refreshToken
            }.first()
        }
    }

    override suspend fun clearRefreshToken() {
        runBlocking { // Use runBlocking for synchronous DataStore operation
            context.dataStore.updateData { currentUserPreferences -> // Use updateData instead of edit
                currentUserPreferences.copy(refreshToken = null) // Clear refresh token by setting to null
            }
        }
    }
}