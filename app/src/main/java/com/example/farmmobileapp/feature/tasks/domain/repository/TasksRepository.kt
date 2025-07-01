package com.example.farmmobileapp.feature.tasks.domain.repository

import com.example.farmmobileapp.feature.tasks.data.model.CreateTaskCommentDto
import com.example.farmmobileapp.feature.tasks.data.model.TaskCommentDto
import com.example.farmmobileapp.feature.tasks.data.model.TaskDto
import com.example.farmmobileapp.feature.tasks.data.model.enums.TaskStatus
import com.example.farmmobileapp.util.Resource

interface TasksRepository {
    suspend fun getMyTasks(): Resource<List<TaskDto>>
    suspend fun getTaskById(taskId: String): Resource<TaskDto>
    suspend fun updateStatus(taskId: String, status: TaskStatus): Resource<Unit>
    suspend fun addComment(taskCommentDto: CreateTaskCommentDto): Resource<Unit>
    suspend fun getComments(taskId: String): Resource<List<TaskCommentDto>>
}