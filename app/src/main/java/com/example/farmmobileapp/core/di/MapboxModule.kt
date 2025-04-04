package com.example.farmmobileapp.core.di

import com.example.farmmobileapp.core.data.repository.MapboxTokenRepositoryImpl
import com.example.farmmobileapp.core.domain.repository.MapboxTokenRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class MapboxModule {

    // Uncomment the following code to provide the MapboxTokenRepository
    // and remove the commented-out code in the MapboxTokenRepositoryImpl class.

     @Binds
     @Singleton
     abstract fun bindMapboxTokenRepository(mapboxTokenRepositoryImpl: MapboxTokenRepositoryImpl): MapboxTokenRepository

    // Uncomment this function to provide the MapboxTokenRepositoryImpl

    // @Provides
    // fun provideMapboxTokenRepository(
    //     tokenManager: TokenManager
    // ): MapboxTokenRepository {
    //     return MapboxTokenRepositoryImpl(tokenManager)
    // }
}