package com.example.farmmobileapp.core.di

import com.example.farmmobileapp.core.storage.AuthenticationManager
import com.example.farmmobileapp.core.storage.TokenRepository
import com.example.farmmobileapp.feature.auth.data.api.IdentityApi
import com.example.farmmobileapp.feature.auth.domain.repository.IdentityRepository
import com.example.farmmobileapp.feature.auth.domain.repository.IdentityRepositoryImpl
import com.example.farmmobileapp.feature.users.data.api.UsersApi
import com.example.farmmobileapp.util.StringResourcesHelper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class AuthModule {
    // Uncomment the following code to provide the IdentityRepository
    // and remove the commented-out code in the IdentityRepositoryImpl class.

//    @Binds
//    abstract fun bindIdentityRepository(identityRepositoryImpl: IdentityRepositoryImpl): IdentityRepository

//     Uncomment this function to provide the IdentityRepositoryImpl

    @Provides
    fun provideIdentityRepository(
        identityApi: IdentityApi,
        usersApi: UsersApi,
        tokenRepository: TokenRepository,
        authenticationManager: AuthenticationManager,
        stringResourcesHelper: StringResourcesHelper
    ): IdentityRepository {
        return IdentityRepositoryImpl(
            identityApi,
            usersApi,
            tokenRepository,
            authenticationManager,
            stringResourcesHelper
        )
    }
}
