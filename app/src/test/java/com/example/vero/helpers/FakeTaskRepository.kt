package com.example.vero.helpers

import com.example.vero.db.entity.TaskModel
import com.example.vero.iteractor.TaskRepository
import com.example.vero.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeTaskRepository : TaskRepository {

    private val tasks = mutableListOf(firstTaskModel, secondTaskModel)
    // Track how many times refreshTasks() is called
    var refreshCount = 0

    override suspend fun login(username: String, password: String): Resource<String> {
        return Resource.Success("fake_token")
    }

    override fun getTasks(): Flow<Resource<List<TaskModel>>> = flow {
        emit(Resource.Success(tasks))
    }

    override fun searchTasks(query: String): Flow<Resource<List<TaskModel>>> = flow {
        val filtered = if (query.isBlank()) {
            tasks
        } else {
            tasks.filter { it.title!!.contains(query, ignoreCase = true) }
        }
        emit(Resource.Success(filtered))
    }

    override suspend fun refreshTasks() {
        refreshCount++
    }
}