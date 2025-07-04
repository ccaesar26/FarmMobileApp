package com.example.farmmobileapp.feature.fields.domain.repository

import com.example.farmmobileapp.feature.fields.data.model.Field
import com.example.farmmobileapp.util.Resource

interface FieldsRepository {
    suspend fun getField(fieldId: String): Resource<Field>
    suspend fun getFields(): Resource<List<Field>>
}