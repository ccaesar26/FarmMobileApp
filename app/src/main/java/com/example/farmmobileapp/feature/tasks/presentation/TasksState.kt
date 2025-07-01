package com.example.farmmobileapp.feature.tasks.presentation

import com.example.farmmobileapp.feature.tasks.data.model.enums.TaskStatus

data class TasksState(
    val isLoading: Boolean = false,
    val tasksByStatus: Map<TaskStatus, List<TaskWithField>> = emptyMap(),
    val error: String? = null,
    val comments: List<TaskCommentWithUser> = emptyList(),
    val isCommentsLoading: Boolean = false,
    val commentsError: String? = null,
    val isAddingComment: Boolean = false // To show a loader when posting a comment
)