package com.example.vero.iteractor

import com.example.vero.api.TaskApiService
import com.example.vero.api.dtos.AuthRequest
import com.example.vero.db.dao.TaskDao
import com.example.vero.db.entity.TaskModel
import com.example.vero.utils.Resource
import com.example.vero.utils.safeApiCall
import com.example.vero.utils.toModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import timber.log.Timber
import javax.inject.Inject

interface TaskRepository {
    suspend fun login(username: String, password: String): Resource<String>
    fun getTasks(): Flow<Resource<List<TaskModel>>>
    fun searchTasks(query: String): Flow<Resource<List<TaskModel>>>
    suspend fun refreshTasks()
}

class TaskRepositoryImpl @Inject constructor(
    private val api: TaskApiService,
    private val dao: TaskDao,
    private val authDataStore: AuthDataStore
) : TaskRepository {

    override suspend fun login(username: String, password: String): Resource<String> {
        return try {
            val authRequest = AuthRequest(username, password)
            val response = api.login(authRequest = authRequest)
            val token = response.oauth.access_token
            authDataStore.saveAuthToken(token)

            Timber.d("Login successful. Token saved.")
            Resource.Success(token)
        } catch (e: Exception) {
            Timber.e(e, "Login failed for user: $username")
            Resource.Error("Login failed: ${e.localizedMessage}")
        }
    }

    override fun getTasks(): Flow<Resource<List<TaskModel>>> = flow {
        Timber.d("Loading tasks...")

        val isLoggedIn = ensureLoggedIn()
        if (!isLoggedIn) {
            Timber.w("User not logged in or no internet connection.")
            emit(Resource.Error("Something went wrong."))
            return@flow
        }

        try {
            dao.getAllTasks().collect { tasks ->
                Timber.d("Collected ${tasks.size} tasks from local DB.")
                if (tasks.isEmpty()) {
                    Timber.d("No local tasks found. Fetching from network...")
                    when (val networkResult = safeApiCall { api.getTasks() }) {
                        is Resource.Success -> {
                            val models = networkResult.data.map { it.toModel() }
                            dao.deleteAllTasks()
                            dao.insertTasks(models)
                            Timber.d("Fetched ${models.size} tasks from API and updated DB.")
                            emit(Resource.Success(models))
                        }
                        is Resource.Error -> {
                            Timber.e("Error fetching tasks from API: ${networkResult.message}")
                            emit(Resource.Error(networkResult.message ?: "Fetch error"))
                        }
                        is Resource.Loading -> {}
                    }
                } else {
                    emit(Resource.Success(tasks))
                }
            }
        } catch (e: Exception) {
            Timber.e(e, "Exception during task loading.")
            emit(Resource.Error("Error loading tasks: ${e.message}"))
        }
    }

    override fun searchTasks(query: String): Flow<Resource<List<TaskModel>>> {
        Timber.d("Searching tasks with query: $query")
        return dao.searchTasks(query).map {
            Timber.d("Found ${it.size} matching tasks.")
            Resource.Success(it)
        }
    }

    override suspend fun refreshTasks() {
        Timber.d("Refreshing tasks...")
        when (val result = safeApiCall { api.getTasks() }) {
            is Resource.Success -> {
                result.data?.let { taskList ->
                    Timber.d("Fetched ${taskList.size} tasks from API for refresh.")
                    dao.deleteAllTasks()
                    dao.insertTasks(taskList.map { it.toModel() })
                    Timber.d("Database updated with fresh tasks.")
                }
            }
            is Resource.Error -> Timber.e("Failed to refresh tasks: ${result.message}")
            else -> Timber.d("Refresh returned loading or unknown state.")
        }
    }

    suspend fun ensureLoggedIn(): Boolean {
        val token = authDataStore.authToken.firstOrNull()
        Timber.d("Checking auth token...")
        return if (token.isNullOrEmpty()) {
            Timber.d("Token empty. Attempting fallback login...")
            val loginResult = login("365", "1")
            val success = loginResult is Resource.Success
            Timber.d("Fallback login ${if (success) "succeeded" else "failed"}.")
            success
        } else {
            Timber.d("Token found. Already logged in.")
            true
        }
    }
}