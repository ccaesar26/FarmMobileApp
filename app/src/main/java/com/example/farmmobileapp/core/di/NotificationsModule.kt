package com.example.farmmobileapp.core.di

import com.example.farmmobileapp.feature.notifications.domain.repository.NotificationsRepository
import com.example.farmmobileapp.feature.notifications.domain.repository.NotificationsRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class NotificationsModule {
    @Binds
    @Singleton
    abstract fun bindNotificationsRepository(notificationsRepositoryImpl: NotificationsRepositoryImpl): NotificationsRepository
}