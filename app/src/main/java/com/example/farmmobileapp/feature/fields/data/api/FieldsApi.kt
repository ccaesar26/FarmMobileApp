package com.example.farmmobileapp.feature.fields.data.api

import com.example.farmmobileapp.feature.fields.data.model.Field
import com.example.farmmobileapp.util.Resource

interface FieldsApi {
    suspend fun getField(fieldId: String): Resource<Field>
}