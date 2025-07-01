package com.example.farmmobileapp.feature.fields.domain.repository

import com.example.farmmobileapp.feature.fields.data.api.FieldsApi
import com.example.farmmobileapp.feature.fields.data.model.Field
import com.example.farmmobileapp.util.Resource
import javax.inject.Inject

class FieldsRepositoryImpl @Inject constructor(
    private val fieldsApi: FieldsApi
) : FieldsRepository {

    override suspend fun getField(fieldId: String): Resource<Field> {
        return fieldsApi.getField(fieldId) // Use FieldsApi to get Field
    }

    override suspend fun getFields(): Resource<List<Field>> {
        return fieldsApi.getFields() // Use FieldsApi to get list of Fields
    }
}