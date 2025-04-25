package com.example.farmmobileapp.core.di

import com.example.farmmobileapp.feature.tasks.domain.repository.FieldsRepository
import com.example.farmmobileapp.feature.tasks.domain.repository.FieldsRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class FieldsModule {
    @Binds
    @Singleton
    abstract fun bindFieldsApi(fieldsRepositoryImpl: FieldsRepositoryImpl): FieldsRepository
}

