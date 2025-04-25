package com.example.farmmobileapp.core.di

import android.content.Context
import com.example.farmmobileapp.core.data.remote.api.ConfigApi
import com.example.farmmobileapp.core.data.remote.api.KtorConfigApi
import com.example.farmmobileapp.feature.auth.data.api.IdentityApi
import com.example.farmmobileapp.feature.auth.data.api.KtorIdentityApi
import com.example.farmmobileapp.core.storage.AuthenticationManager
import com.example.farmmobileapp.core.storage.TokenRepository
import com.example.farmmobileapp.feature.tasks.data.api.FieldsApi
import com.example.farmmobileapp.feature.tasks.data.api.KtorFieldsApi
import com.example.farmmobileapp.feature.tasks.data.api.KtorTasksApi
import com.example.farmmobileapp.feature.tasks.data.api.TasksApi
import com.example.farmmobileapp.feature.users.data.api.KtorUsersApi
import com.example.farmmobileapp.feature.users.data.api.UsersApi
import com.example.farmmobileapp.util.StringResourcesHelper
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

//    @Provides
//    @Singleton
//    fun provideHttpClient(): HttpClient {
//        return HttpClient(Android) {
//            install(ContentNegotiation) {
//                json(Json { ignoreUnknownKeys = true })
//            }
//        }
//    }

    @Provides
    @Singleton
    fun provideIdentityApi(httpClient: HttpClient): IdentityApi {
        return KtorIdentityApi(httpClient)
    }

    @Provides
    @Singleton
    fun provideUsersApi(httpClient: HttpClient, tokenRepository: TokenRepository): UsersApi {
        return KtorUsersApi(httpClient, tokenRepository)
    }

    @Provides
    @Singleton
    fun provideTasksApi(httpClient: HttpClient, tokenRepository: TokenRepository): TasksApi {
        return KtorTasksApi(httpClient, tokenRepository)
    }

    @Provides
    @Singleton
    fun provideFieldsApi(httpClient: HttpClient, tokenRepository: TokenRepository): FieldsApi {
        return KtorFieldsApi(httpClient, tokenRepository)
    }

    @Provides
    @Singleton
    fun provideConfigApi(httpClient: HttpClient, tokenRepository: TokenRepository): ConfigApi {
        return KtorConfigApi(httpClient, tokenRepository)
    }

    @Provides
    @Singleton
    fun provideStringResourcesHelper(@ApplicationContext context: Context): StringResourcesHelper {
        return StringResourcesHelper(context)
    }

    @Provides
    @Singleton
    fun provideAuthenticationManager(tokenRepository: TokenRepository, usersApi: UsersApi): AuthenticationManager {
        return AuthenticationManager(tokenRepository, usersApi)
    }
}