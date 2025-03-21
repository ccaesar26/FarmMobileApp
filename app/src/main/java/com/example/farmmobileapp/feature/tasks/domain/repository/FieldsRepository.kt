package com.example.farmmobileapp.feature.tasks.domain.repository

import com.example.farmmobileapp.feature.tasks.data.model.Field
import com.example.farmmobileapp.util.Resource

interface FieldsRepository {
    suspend fun getField(fieldId: String): Resource<Field>
}