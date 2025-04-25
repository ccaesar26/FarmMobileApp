package com.example.farmmobileapp.feature.tasks.domain.repository

import com.example.farmmobileapp.feature.tasks.data.api.TasksApi
import com.example.farmmobileapp.feature.tasks.data.model.TaskDto
import com.example.farmmobileapp.feature.tasks.data.model.Field
import com.example.farmmobileapp.feature.tasks.data.model.enums.TaskStatus
import com.example.farmmobileapp.util.Resource
import javax.inject.Inject

class TasksRepositoryImpl @Inject constructor(
    private val tasksApi: TasksApi
) : TasksRepository {

    override suspend fun getMyTasks(): Resource<List<TaskDto>> {
        return tasksApi.getMyTasks()
    }

    override suspend fun getTaskById(taskId: String): Resource<TaskDto> {
        return tasksApi.getTaskById(taskId)
    }

    override suspend fun updateStatus(taskId: String, status: TaskStatus): Resource<Unit> {
        return tasksApi.updateStatus(taskId, status)
    }
}