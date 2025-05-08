package com.example.farmmobileapp.core.di

import com.example.farmmobileapp.feature.users.domain.repository.UserRepository
import com.example.farmmobileapp.feature.users.domain.repository.UserRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class UsersModule {
    @Binds
    @Singleton
    abstract fun bindUsersRepository(usersRepositoryImpl: UserRepositoryImpl): UserRepository
}