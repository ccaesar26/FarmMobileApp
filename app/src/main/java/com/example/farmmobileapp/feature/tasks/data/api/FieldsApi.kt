package com.example.farmmobileapp.feature.tasks.data.api

import com.example.farmmobileapp.feature.tasks.data.model.Field
import com.example.farmmobileapp.util.Resource

interface FieldsApi {
    suspend fun getField(fieldId: String): Resource<Field>
}