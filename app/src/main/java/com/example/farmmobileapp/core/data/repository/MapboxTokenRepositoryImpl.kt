package com.example.farmmobileapp.core.data.repository

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.farmmobileapp.core.data.remote.api.ConfigApi
import com.example.farmmobileapp.core.data.preferences.extensions.mapboxDataStore
import com.example.farmmobileapp.core.domain.repository.MapboxTokenRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.flow.first

class MapboxTokenRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val configApi: ConfigApi
) : MapboxTokenRepository {

    private val mapboxTokenKey = stringPreferencesKey("mapbox_access_token")

    override suspend fun getToken(): String {
        Log.d("MapboxTokenRepository", "Fetching Mapbox token...")
        val preferences = context.mapboxDataStore.data.first()
        val cachedToken = preferences[mapboxTokenKey]

        return if (!cachedToken.isNullOrEmpty()) {
            Log.d("MapboxTokenRepository", "Using cached token: $cachedToken")
            cachedToken
        } else {
            Log.d("MapboxTokenRepository", "Fetching new token from API")
            val response = configApi.getMapboxAccessToken()
            val token = response.value
            saveToken(token)
            token
        }
    }

    override suspend fun saveToken(token: String) {
        context.mapboxDataStore.edit { prefs ->
            prefs[mapboxTokenKey] = token
        }
    }
}
