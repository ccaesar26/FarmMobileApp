package com.example.farmmobileapp.core.di

import com.example.farmmobileapp.feature.reports.domain.repository.ReportsRepository
import com.example.farmmobileapp.feature.reports.domain.repository.ReportsRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ReportsModule {
    @Binds
    @Singleton
    abstract fun bindFieldsApi(reportsRepositoryImpl: ReportsRepositoryImpl): ReportsRepository
}