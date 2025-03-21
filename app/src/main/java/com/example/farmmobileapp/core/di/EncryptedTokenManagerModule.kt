package com.example.farmmobileapp.core.di

import android.content.Context
import com.example.farmmobileapp.core.storage.EncryptedTokenManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class EncryptedTokenManagerModule {

    @Provides
    @Singleton
    fun provideEncryptedTokenManager(@ApplicationContext context: Context): EncryptedTokenManager {
        return EncryptedTokenManager(context)
    }
}