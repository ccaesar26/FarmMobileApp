package com.example.farmmobileapp.di

import com.example.farmmobileapp.data.api.IdentityApi
import com.example.farmmobileapp.data.api.KtorIdentityApi
import com.example.farmmobileapp.data.storage.EncryptedTokenManager
import com.example.farmmobileapp.data.storage.TokenManager
import com.example.farmmobileapp.ui.viewmodel.LoginViewModel
import com.example.farmmobileapp.ui.viewmodel.MainViewModel
import com.example.farmmobileapp.util.StringResourcesHelper
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single { androidContext() }
    single { StringResourcesHelper(get()) }
    single<TokenManager> { EncryptedTokenManager(get()) }

    single {
        HttpClient(Android) { // Ktor HttpClient setup
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
        }
    }
    single<IdentityApi> { KtorIdentityApi(get()) }

    viewModel { LoginViewModel(get(), get(), get()) }
    viewModel { MainViewModel() }
}