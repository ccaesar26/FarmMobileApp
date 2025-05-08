package com.example.farmmobileapp.feature.tasks.presentation

import com.example.farmmobileapp.feature.fields.data.model.Field
import com.example.farmmobileapp.feature.tasks.data.model.TaskDto

data class TaskWithField(
    val task: TaskDto,
    val field: Field?
)
