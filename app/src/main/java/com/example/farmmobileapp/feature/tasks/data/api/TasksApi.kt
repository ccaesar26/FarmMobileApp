package com.example.farmmobileapp.feature.tasks.data.api

import com.example.farmmobileapp.feature.tasks.data.model.TaskDto
import com.example.farmmobileapp.feature.tasks.data.model.enums.TaskStatus
import com.example.farmmobileapp.util.Resource

interface TasksApi {
    suspend fun getMyTasks(): Resource<List<TaskDto>>
    suspend fun getTaskById(id: String): Resource<TaskDto>
    suspend fun updateStatus(taskId: String, status: TaskStatus): Resource<Unit>
}