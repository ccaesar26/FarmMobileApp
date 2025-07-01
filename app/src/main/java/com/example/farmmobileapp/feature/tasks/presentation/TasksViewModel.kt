package com.example.farmmobileapp.feature.tasks.presentation

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.farmmobileapp.BuildConfig
import com.example.farmmobileapp.feature.tasks.data.model.TaskCommentDto // Make sure this is imported
import com.example.farmmobileapp.feature.tasks.data.model.enums.TaskStatus
import com.example.farmmobileapp.feature.fields.domain.repository.FieldsRepository
import com.example.farmmobileapp.feature.tasks.data.model.CreateTaskCommentDto
import com.example.farmmobileapp.feature.tasks.domain.repository.TasksRepository
import com.example.farmmobileapp.feature.users.domain.repository.UserRepository
import com.example.farmmobileapp.util.Resource
import com.microsoft.signalr.HubConnection
import com.microsoft.signalr.HubConnectionBuilder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TasksViewModel @Inject constructor(
    private val tasksRepository: TasksRepository,
    private val fieldsRepository: FieldsRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _state = MutableStateFlow(TasksState())
    val state: StateFlow<TasksState> = _state.asStateFlow()

    private lateinit var hubConnection: HubConnection

    init {
        setupSignalR()
        startSignalRConnection()
        loadTasks()
    }

    private fun setupSignalR() {
        hubConnection = HubConnectionBuilder.create("${BuildConfig.SIGNALR_HUB_BASE_URL}/taskHub")
            .withTransport(com.microsoft.signalr.TransportEnum.WEBSOCKETS)
            .shouldSkipNegotiate(true)
            .build()

        hubConnection.on("TaskCreated") {
            Log.d("SignalR", "Received TaskCreated notification")
            viewModelScope.launch {
                loadTasks()
            }
        }

        hubConnection.on("TaskUpdated") {
            Log.d("SignalR", "Received TaskUpdated notification")
            viewModelScope.launch { loadTasks() }
        }

        // Add this if your backend signals new comments for a specific task
        hubConnection.on("CommentAdded", { taskId: String -> // Assuming backend sends taskId
            Log.d("SignalR", "Received CommentAdded notification for task: $taskId")
            // Only refresh comments if they are currently loaded for this task
            // This logic might need refinement based on how you manage current task context
            // For now, let's assume if comments are loaded, they are for the active task.
            // A more robust way would be to check if state.currentSelectedTaskId == taskId
            if (_state.value.comments.isNotEmpty() && _state.value.comments.firstOrNull()?.taskComment?.taskId == taskId) {
                viewModelScope.launch { loadComments(taskId) }
            }
        }, String::class.java)
    }

    private fun stopSignalRConnection() {
        viewModelScope.launch {
            try {
                hubConnection.stop().blockingAwait()
                Log.d("SignalR", "SignalR Connection stopped")
            } catch (e: Exception) {
                Log.e("SignalR", "Error stopping SignalR connection: ", e)
            }
        }
    }

    private fun startSignalRConnection() {
        viewModelScope.launch {
            try {
                hubConnection.start().blockingAwait()
                Log.d("SignalR", "SignalR Connection started")
            } catch (e: Exception) {
                Log.e("SignalR", "Error starting SignalR connection: ", e)
            }
        }
    }

    fun loadTasks() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            when (val result = tasksRepository.getMyTasks()) {
                is Resource.Success -> {
                    val taskDtos = result.data ?: emptyList()
                    val tasksWithFields = mutableListOf<TaskWithField>()
                    taskDtos.forEach { taskDto ->
                        taskDto.fieldId?.let {
                            fieldsRepository.getField(it).let { fieldResult ->
                                val field = when (fieldResult) {
                                    is Resource.Success -> fieldResult.data
                                    is Resource.Error -> null
                                    is Resource.Loading -> null
                                }
                                tasksWithFields.add(TaskWithField(taskDto, field))
                            }
                        } ?: run {
                            tasksWithFields.add(TaskWithField(taskDto, null))
                        }
                    }

                    val tasksByStatusMap = tasksWithFields
                        .groupBy { it.task.status }
                        .toSortedMap<TaskStatus, List<TaskWithField>>(compareBy { it.ordinal })

                    _state.value = _state.value.copy(
                        isLoading = false,
                        tasksByStatus = tasksByStatusMap,
                        error = null
                    )
                }
                is Resource.Error -> {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        tasksByStatus = emptyMap(),
                        error = result.message ?: "Failed to load tasks"
                    )
                }
                is Resource.Loading<*> -> {
                    _state.value = _state.value.copy(isLoading = true)
                }
            }
        }
    }

    suspend fun getTaskById(taskId: String): TaskWithField? {
        _state.value = _state.value.copy(isLoading = true)
        // Clear previous comments when fetching a new task detail
        _state.value = _state.value.copy(comments = emptyList(), commentsError = null)

        return when (val result = tasksRepository.getTaskById(taskId)) {
            is Resource.Success -> {
                val taskDto = result.data ?: return null
                val field = taskDto.fieldId?.let { fieldId ->
                    when (val fieldResult = fieldsRepository.getField(fieldId)) {
                        is Resource.Success -> fieldResult.data
                        else -> null
                    }
                }
                _state.value = _state.value.copy(isLoading = false, error = null)
                TaskWithField(taskDto, field)
            }
            is Resource.Error -> {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = result.message ?: "Failed to load task"
                )
                Log.e("TasksViewModel", "Error loading task with ID $taskId: ${result.message}")
                null
            }
            is Resource.Loading -> {
                _state.value = _state.value.copy(isLoading = true)
                null
            }
        }
    }

    fun loadComments(taskId: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isCommentsLoading = true, commentsError = null)
            when (val result = tasksRepository.getComments(taskId)) {
                is Resource.Success -> {
                    val taskCommentDtos = result.data ?: emptyList()
                    val commentsWithUsers = taskCommentDtos.map { commentDto ->
                        TaskCommentWithUser(
                            taskComment = commentDto,
                            userName = userRepository.getUsernameById(commentDto.userId)
                        )
                    }
                    _state.value = _state.value.copy(
                        isCommentsLoading = false,
                        comments = commentsWithUsers,
                    )
                }
                is Resource.Error -> {
                    _state.value = _state.value.copy(
                        isCommentsLoading = false,
                        commentsError = result.message ?: "Failed to load comments"
                    )
                }
                is Resource.Loading -> {
                    // Handled by isCommentsLoading = true at the start
                }
            }
        }
    }

    fun addComment(taskId: String, content: String) {
        if (content.isBlank()) return // Don't add empty comments

        viewModelScope.launch {
            _state.value = _state.value.copy(isAddingComment = true)

            val commentDto = CreateTaskCommentDto(
                taskId = taskId,
                comment = content,
            )
            when (val result = tasksRepository.addComment(commentDto)) {
                is Resource.Success -> {
                    _state.value = _state.value.copy(isAddingComment = false)
                    // Reload comments to see the new one
                    loadComments(taskId)
                }
                is Resource.Error -> {
                    _state.value = _state.value.copy(
                        isAddingComment = false,
                        // Optionally, set an error specific to adding comment
                        commentsError = result.message ?: "Failed to add comment"
                    )
                    Log.e("TasksViewModel", "Error adding comment: ${result.message}")
                }
                is Resource.Loading -> {
                    // Handled by isAddingComment = true
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        stopSignalRConnection()
    }

    fun updateTaskStatus(taskId: String, newStatus: TaskStatus) {
        viewModelScope.launch {
            when (val result = tasksRepository.updateStatus(taskId, newStatus)) {
                is Resource.Success -> {
                    Log.d("TasksViewModel", "Task status updated successfully")
                    loadTasks()
                }
                is Resource.Error -> {
                    Log.e("TasksViewModel", "Error updating task status: ${result.message}")
                }
                is Resource.Loading -> { /* Handle loading */ }
            }
        }
    }
}