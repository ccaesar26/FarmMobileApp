package com.example.farmmobileapp.feature.tasks.data.model

import com.example.farmmobileapp.feature.tasks.data.model.enums.RecurrenceType
import com.example.farmmobileapp.feature.tasks.data.model.enums.RecurrenceTypeSerializer
import com.example.farmmobileapp.feature.tasks.data.model.enums.TaskPriority
import com.example.farmmobileapp.feature.tasks.data.model.enums.TaskPrioritySerializer
import com.example.farmmobileapp.feature.tasks.data.model.enums.TaskStatus
import com.example.farmmobileapp.feature.tasks.data.model.enums.TaskStatusSerializer
import kotlinx.serialization.Serializable

@Serializable
data class TaskDto(
    val id: String,
    val title: String,
    val description: String? = null,
    val dueDate: String? = null,
    @Serializable(with = TaskPrioritySerializer::class) val priority: TaskPriority,
    @Serializable(with = TaskStatusSerializer::class) val status: TaskStatus,
    val assignedUserIds: List<String>,
    val categoryId: String? = null,
    val categoryName: String? = null,
    @Serializable(with = RecurrenceTypeSerializer::class) val recurrence: RecurrenceType,
    val recurrenceEndDate: String? = null,
    val fieldId: String? = null,
    val cropId: String? = null,
    val commentsCount: Int,
    val createdAt: String
)
