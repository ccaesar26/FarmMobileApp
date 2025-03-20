package com.example.farmmobileapp.di

import android.content.Context
import com.example.farmmobileapp.data.api.IdentityApi
import com.example.farmmobileapp.data.api.KtorIdentityApi
import com.example.farmmobileapp.data.storage.EncryptedTokenManager
import com.example.farmmobileapp.data.storage.TokenManager
import com.example.farmmobileapp.util.StringResourcesHelper
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModules {

    @Provides
    @Singleton
    fun provideHttpClient(): HttpClient {
        return HttpClient(Android) {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
        }
    }

    @Provides
    @Singleton
    fun provideIdentityApi(httpClient: HttpClient): IdentityApi {
        return KtorIdentityApi(httpClient)
    }

    @Provides
    @Singleton
    fun provideStringResourcesHelper(@ApplicationContext context: Context): StringResourcesHelper {
        return StringResourcesHelper(context)
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class TokenManagerModule {

    @Binds
    @Singleton
    abstract fun bindTokenManager(encryptedTokenManager: EncryptedTokenManager): TokenManager
}

@Module
@InstallIn(SingletonComponent::class)
class EncryptedTokenManagerModule {

    @Provides
    @Singleton
    fun provideEncryptedTokenManager(@ApplicationContext context: Context): EncryptedTokenManager {
        return EncryptedTokenManager(context)
    }
}