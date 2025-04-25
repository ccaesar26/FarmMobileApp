package com.example.farmmobileapp.feature.users.domain.repository

import com.example.farmmobileapp.R
import com.example.farmmobileapp.feature.users.data.api.UsersApi
import com.example.farmmobileapp.feature.users.data.model.UserRoleResponse
import com.example.farmmobileapp.util.Resource
import com.example.farmmobileapp.util.StringResourcesHelper

class UserRepositoryImpl(
    private val usersApi: UsersApi,
    private val stringResourcesHelper: StringResourcesHelper
) : UserRepository {
    override suspend fun getMeUser(): Resource<UserRoleResponse> {
        return when (val result = usersApi.getMe()) {
            is Resource.Success -> {
                if (result.data != null) {
                    Resource.Success(result.data)
                } else {
                    Resource.Error(stringResourcesHelper.getString(R.string.login_error_user_info))
                }
            }

            is Resource.Error -> {
                Resource.Error(
                    result.message
                        ?: stringResourcesHelper.getString(R.string.login_error_user_info)
                )
            }

            is Resource.Loading<*> -> {
                // Should not happen
                Resource.Error(stringResourcesHelper.getString(R.string.login_error_user_info))
            }
        }
    }
}