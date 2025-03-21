package com.example.farmmobileapp.core.di

import com.example.farmmobileapp.feature.tasks.domain.repository.TasksRepository
import com.example.farmmobileapp.feature.tasks.domain.repository.TasksRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class TasksModule {
    @Binds
    @Singleton
    abstract fun bindTasksApi(tasksRepositoryImpl: TasksRepositoryImpl): TasksRepository
}