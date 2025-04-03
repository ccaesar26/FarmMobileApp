package com.example.farmmobileapp.feature.tasks.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.farmmobileapp.feature.tasks.domain.repository.FieldsRepository
import com.example.farmmobileapp.feature.tasks.domain.repository.TasksRepository
import com.example.farmmobileapp.util.Resource
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

    init {
        loadTasks()
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

                    val tasksByStatusMap = tasksWithFields.groupBy { it.task.status }

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
}