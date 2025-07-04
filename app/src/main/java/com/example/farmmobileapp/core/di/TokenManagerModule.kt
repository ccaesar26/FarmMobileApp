package com.example.farmmobileapp.core.di

import com.example.farmmobileapp.core.storage.EncryptedTokenRepository
import com.example.farmmobileapp.core.storage.TokenRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class TokenManagerModule {

    @Binds
    @Singleton
    abstract fun bindTokenManager(encryptedTokenManager: EncryptedTokenRepository): TokenRepository
}