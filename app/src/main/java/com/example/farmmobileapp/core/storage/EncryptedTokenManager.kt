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

class EncryptedTokenManager(private val context: Context) : TokenManager {

    override suspend fun saveToken(token: String) {
        // Use runBlocking to perform synchronous DataStore operation (for simplicity in TokenManager)
        runBlocking {
            context.dataStore.updateData { currentUserPreferences -> // Use updateData instead of edit
                currentUserPreferences.copy(token = token) // Update token using copy method
            }
        }
    }

    override suspend fun getToken(): String? {
        return runBlocking { // Use runBlocking for synchronous DataStore operation
            context.dataStore.data.map { userPreferences ->
                userPreferences.token
            }.first()
        }
    }

    override suspend fun clearToken() {
        runBlocking { // Use runBlocking for synchronous DataStore operation
            context.dataStore.updateData { currentUserPreferences -> // Use updateData instead of edit
                currentUserPreferences.copy(token = null) // Clear token by setting to null
            }
        }
    }
}