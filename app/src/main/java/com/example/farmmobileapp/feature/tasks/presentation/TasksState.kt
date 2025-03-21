package com.example.farmmobileapp.feature.tasks.presentation

import com.example.farmmobileapp.feature.tasks.data.model.TaskDto

data class TasksState(
    val isLoading: Boolean = false,
    val tasks: List<TaskDto> = emptyList(),
    val error: String? = null
)