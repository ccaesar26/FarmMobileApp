package com.example.farmmobileapp.feature.users.domain.repository

import android.util.Log
import com.example.farmmobileapp.R
import com.example.farmmobileapp.feature.users.data.api.UserProfileApi
import com.example.farmmobileapp.feature.users.data.api.UsersApi
import com.example.farmmobileapp.feature.users.data.model.UserProfile
import com.example.farmmobileapp.feature.users.data.model.UserProfileResponse
import com.example.farmmobileapp.feature.users.data.model.UserResponse
import com.example.farmmobileapp.feature.users.data.model.UserRoleResponse
import com.example.farmmobileapp.util.Resource
import com.example.farmmobileapp.util.StringResourcesHelper
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val usersApi: UsersApi,
    private val userProfileApi: UserProfileApi,
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

    override suspend fun getUsernameById(userId: String): String {
        return usersApi.getUsernameById(userId).data?.username ?: "Unknown User"
    }

    override suspend fun getUser(): Resource<UserProfile> {
        return when (val userResult = usersApi.getUser()) {
            is Resource.Error<*> -> {
                Log.e(
                    "UserRepositoryImpl",
                    "Error fetching user profile: ${userResult.message}"
                )
                Resource.Error(
                    userResult.message
                        ?: stringResourcesHelper.getString(R.string.get_user_error)
                )
            }

            is Resource.Loading<*> -> {
                Log.d("UserRepositoryImpl", "Loading user profile")
                Resource.Error(
                    stringResourcesHelper.getString(R.string.login_error_user_info)
                )
            }

            is Resource.Success<*> -> {
                Log.d("UserRepositoryImpl", "User profile loaded successfully")
                val userResponse = userResult.data
                if (userResponse != null) {
                    val userProfileResult = userProfileApi.getUserProfile()
                    Resource.Success<UserProfile>(
                        UserProfile(
                            userId = userResponse.id,
                            userProfileId = userProfileResult.id,
                            username = userResponse.username,
                            email = userResponse.email,
                            role = userResponse.role,
                            name = userProfileResult.name,
                            dateOfBirth = userProfileResult.dateOfBirth,
                            gender = userProfileResult.gender,
                            attributeNames = userProfileResult.attributeNames
                        )
                    )
                } else {
                    Resource.Error(stringResourcesHelper.getString(R.string.login_error_user_info))
                }
            }
        }
    }
}