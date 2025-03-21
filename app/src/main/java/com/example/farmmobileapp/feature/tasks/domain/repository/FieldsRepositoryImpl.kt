package com.example.farmmobileapp.feature.tasks.domain.repository

import com.example.farmmobileapp.feature.tasks.data.api.FieldsApi
import com.example.farmmobileapp.feature.tasks.data.model.Field
import com.example.farmmobileapp.util.Resource
import javax.inject.Inject

class FieldsRepositoryImpl @Inject constructor(
    private val fieldsApi: FieldsApi
) : FieldsRepository {

    override suspend fun getField(fieldId: String): Resource<Field> {
        return fieldsApi.getField(fieldId) // Use FieldsApi to get Field
    }
}