package com.example.farmmobileapp.feature.tasks.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.farmmobileapp.BuildConfig
import com.example.farmmobileapp.feature.tasks.data.model.enums.TaskStatus
import com.example.farmmobileapp.feature.tasks.domain.repository.FieldsRepository
import com.example.farmmobileapp.feature.tasks.domain.repository.TasksRepository
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
    private val fieldsRepository: FieldsRepository
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

        hubConnection.on("TaskCreated") { // Register handler for "TaskCreated" event
            Log.d("SignalR", "Received TaskCreated notification")
            viewModelScope.launch {
                loadTasks() // Refetch tasks when notification received
            }
        }

        hubConnection.on("TaskUpdated") { // Register handler for "TaskUpdated" event
            Log.d("SignalR", "Received TaskUpdated notification")
            viewModelScope.launch {
                loadTasks() // Refetch tasks when notification received
            }
        }
    }

    private fun stopSignalRConnection() {
        viewModelScope.launch {
            try {
                hubConnection.stop().blockingAwait() // Stop connection (blocking for shutdown)
                Log.d("SignalR", "SignalR Connection stopped")
            } catch (e: Exception) {
                Log.e("SignalR", "Error stopping SignalR connection: ", e)
                // Handle connection stop error
            }
        }
    }

    private fun startSignalRConnection() {
        viewModelScope.launch {
            try {
                hubConnection.start()
                    .blockingAwait() // Start connection (blocking for initial start)
                Log.d("SignalR", "SignalR Connection started")
            } catch (e: Exception) {
                Log.e("SignalR", "Error starting SignalR connection: ", e)
                // Handle connection start error (e.g., retry, show error message)
            }
        }
    }

    private fun loadTasks() {
        viewModelScope.launch {
            _state.value = state.value.copy(isLoading = true)
            when (val result = tasksRepository.getMyTasks()) {
                is Resource.Success -> {
                    val taskDtos = result.data ?: emptyList()
                    val tasksWithFields = mutableListOf<TaskWithField>()
                    taskDtos.forEach { taskDto ->
                        fieldsRepository.getField(taskDto.fieldId.toString()).let {
                            var field = when (it) {
                                is Resource.Success -> it.data
                                is Resource.Error -> null
                                is Resource.Loading -> null
                            }
                            tasksWithFields.add(TaskWithField(taskDto, field))
                            Log.d(
                                "TasksViewModel",
                                "Task: ${taskDto.title}, Field: ${field?.name ?: "No field found"}"
                            )
                        }
                    }

                    val tasksByStatusMap = tasksWithFields
                        .groupBy {
                            it.task.status
                        }
                        .toSortedMap<TaskStatus, List<TaskWithField>>(
                            compareBy { it.ordinal }
                        )

                    _state.value = state.value.copy(
                        isLoading = false,
                        tasksByStatus = tasksByStatusMap,
                        error = null
                    )
                }

                is Resource.Error -> {
                    _state.value = state.value.copy(
                        isLoading = false,
                        tasksByStatus = emptyMap(),
                        error = result.message ?: "Failed to load tasks"
                    )
                }

                is Resource.Loading<*> -> {
                    _state.value =
                        state.value.copy(isLoading = true) // Already set at start, but for completeness
                }
            }
        }
    }

    suspend fun getTaskById(taskId: String): TaskWithField? {
        _state.value = state.value.copy(isLoading = true)

        return when (val result = tasksRepository.getTaskById(taskId)) {
            is Resource.Success -> {
                val taskDto = result.data ?: return null

                val fieldResult = fieldsRepository.getField(taskDto.fieldId.toString())
                val field = if (fieldResult is Resource.Success) fieldResult.data else null

                Log.d(
                    "TasksViewModel",
                    "Task: ${taskDto.title}, Field: ${field?.name ?: "No field found"}"
                )

                _state.value = state.value.copy(isLoading = false, error = null)
                TaskWithField(taskDto, field)
            }

            is Resource.Error -> {
                _state.value = state.value.copy(
                    isLoading = false,
                    error = result.message ?: "Failed to load task"
                )
                Log.e("TasksViewModel", "Error loading task with ID $taskId: ${result.message}")
                null
            }

            is Resource.Loading -> {
                _state.value = state.value.copy(isLoading = true)
                null
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        stopSignalRConnection() // Stop SignalR connection when ViewModel is cleared
    }

    fun updateTaskStatus(taskId: String, newStatus: TaskStatus) {
        viewModelScope.launch {
            when (val result = tasksRepository.updateStatus(taskId, newStatus)) {
                is Resource.Success -> {
                    Log.d("TasksViewModel", "Task status updated successfully")
                    loadTasks() // Reload tasks after updating status
                }

                is Resource.Error -> {
                    Log.e("TasksViewModel", "Error updating task status: ${result.message}")
                }

                is Resource.Loading -> {
                    // Handle loading state if needed
                }
            }
        }
    }
}