package com.example.vero.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vero.db.entity.TaskModel
import com.example.vero.iteractor.TaskRepository
import com.example.vero.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class TaskViewModel @Inject constructor(
    private val repository: TaskRepository
) : ViewModel() {

    private val _taskState = MutableStateFlow(TaskState())
    val taskState: StateFlow<TaskState> = _taskState

    private val currentSearchQuery = MutableStateFlow<String?>(null)

    init {
        Timber.d("TaskViewModel initialized, loading tasks...")
        loadTasks()
    }

    fun onSearchQueryChanged(query: String) {
        Timber.d("Search query changed: \"$query\"")
        currentSearchQuery.value = query
        if (query.isBlank()) {
            loadTasks()
        } else {
            searchTasks(query)
        }
    }

    fun refreshTasks() {
        viewModelScope.launch {
            Timber.d("Refreshing tasks...")
            _taskState.update { it.copy(isLoading = true, error = null) }
            repository.refreshTasks()
            val query = currentSearchQuery.value
            if (query.isNullOrBlank()) {
                loadTasks()
            } else {
                searchTasks(query)
            }
        }
    }

    fun loadTasks() {
        viewModelScope.launch {
            repository.getTasks()
                .onStart {
                    Timber.d("Loading tasks...")
                    _taskState.update { it.copy(isLoading = true, error = null) }
                }
                .catch { e ->
                    Timber.e(e, "Error loading tasks")
                    _taskState.update { it.copy(isLoading = false, error = e.message ?: "Error loading tasks") }
                }
                .collect { result ->
                    when (result) {
                        is Resource.Loading -> {
                            Timber.d("Tasks loading...")
                            _taskState.update { it.copy(isLoading = true) }
                        }
                        is Resource.Success -> {
                            Timber.d("Tasks loaded successfully: ${result.data?.size ?: 0} tasks")
                            _taskState.update {
                                it.copy(isLoading = false, tasks = result.data ?: emptyList(), error = null)
                            }
                        }
                        is Resource.Error -> {
                            Timber.e("Error loading tasks: ${result.message}")
                            _taskState.update {
                                it.copy(isLoading = false, error = result.message)
                            }
                        }
                    }
                }
        }
    }

    fun searchTasks(query: String) {
        viewModelScope.launch {
            repository.searchTasks(query)
                .onStart {
                    Timber.d("Searching tasks for query: \"$query\"")
                    _taskState.update { it.copy(isLoading = true, error = null) }
                }
                .catch { e ->
                    Timber.e(e, "Error searching tasks")
                    _taskState.update { it.copy(isLoading = false, error = e.message ?: "Error searching tasks") }
                }
                .collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            Timber.d("Search results for \"$query\": ${result.data?.size ?: 0} tasks")
                            _taskState.update {
                                it.copy(isLoading = false, tasks = result.data ?: emptyList(), error = null)
                            }
                        }
                        is Resource.Error -> {
                            Timber.e("Error searching tasks: ${result.message}")
                            _taskState.update {
                                it.copy(isLoading = false, error = result.message)
                            }
                        }
                        else -> Unit
                    }
                }
        }
    }
}

data class TaskState(
    val isLoading: Boolean = false,
    val tasks: List<TaskModel> = emptyList(),
    val error: String? = null
)
