package com.example.vero.iteractor

import com.example.vero.api.TaskApiService
import com.example.vero.api.dtos.AuthRequest
import com.example.vero.api.dtos.AuthResponse
import com.example.vero.api.dtos.OAuthData
import com.example.vero.api.dtos.UserInfo
import com.example.vero.db.dao.TaskDao
import com.example.vero.db.entity.TaskModel
import com.example.vero.helpers.apiTasks
import com.example.vero.helpers.firstTaskModel
import com.example.vero.helpers.secondTaskModel
import com.example.vero.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import io.mockk.coEvery
import io.mockk.coVerifySequence
import io.mockk.mockk
import io.mockk.just
import io.mockk.Runs
import io.mockk.coVerify
import io.mockk.slot


@OptIn(ExperimentalCoroutinesApi::class)
class TaskRepositoryImplTest {

    private val api = mockk<TaskApiService>()
    private val dao = mockk<TaskDao>(relaxed = true)
    private val authDataStore = mockk<AuthDataStore>(relaxed = true)

    private lateinit var repository: TaskRepositoryImpl

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = TaskRepositoryImpl(api, dao, authDataStore)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun loginSuccessSavesToken() = runTest {
        val token = "token123"
        val authRequest = AuthRequest(username = "user", password = "pass")

        val oauthData = OAuthData(
            access_token = token,
            expires_in = 3600,
            token_type = "Bearer",
            scope = null,
            refresh_token = "refreshToken123"
        )

        val userInfo = UserInfo(
            personalNo = 365,
            firstName = "John",
            lastName = "Doe",
            displayName = "John Doe",
            active = true,
            businessUnit = "Gerüstbau"
        )

        val authResponse = AuthResponse(
            oauth = oauthData,
            userInfo = userInfo,
            permissions = listOf("read", "write"),
            apiVersion = "v1",
            showPasswordPrompt = false
        )

        coEvery { api.login( authRequest = authRequest) } returns authResponse
        coEvery { authDataStore.saveAuthToken(token) } just Runs

        val result = repository.login("user", "pass")

        assertTrue(result is Resource.Success)
        coVerify { authDataStore.saveAuthToken(token) }
    }


    @Test
    fun getTasksEmitsTasksFromLocalDB() = runTest {
        // Mock auth token present to skip fallback login
        coEvery { authDataStore.authToken } returns flowOf("token")

        val localTasks = listOf(firstTaskModel, secondTaskModel)
        coEvery { dao.getAllTasks() } returns flowOf(localTasks)

        val flow = repository.getTasks()

        val results = mutableListOf<Resource<List<TaskModel>>>()
        val job = launch(testDispatcher) {
            flow.collect { results.add(it) }
        }

        advanceUntilIdle()
        job.cancel()

        assertTrue(results.isNotEmpty())
        assertTrue(results.first() is Resource.Success)
        assertEquals(localTasks, (results.first() as Resource.Success).data)
    }

    @Test
    fun searchTasksReturnsFilteredTasks() = runTest {
        val query = "abbauen"
        val filteredTasks = listOf(secondTaskModel)

        coEvery { dao.searchTasks(query) } returns flowOf(filteredTasks)

        val flow = repository.searchTasks(query)

        val results = mutableListOf<Resource<List<TaskModel>>>()
        val job = launch(testDispatcher) {
            flow.collect { results.add(it) }
        }
        advanceUntilIdle()
        job.cancel()

        assertTrue(results.isNotEmpty())
        assertTrue(results.first() is Resource.Success)
        assertEquals(filteredTasks, (results.first() as Resource.Success).data)
    }

    @Test
    fun refreshTasksFetchesFromAPIAndUpdatesDB() = runTest {

        coEvery { api.getTasks() } returns apiTasks
        coEvery { dao.deleteAllTasks() } just Runs

        val slot = slot<List<TaskModel>>() // capture the mapped list
        coEvery { dao.insertTasks(capture(slot)) } just Runs

        repository.refreshTasks()

        val insertedTasks = slot.captured
        assertEquals(apiTasks.size, insertedTasks.size)
        assertTrue(insertedTasks.any { it.task == "200 Aufbau" })

        coVerifySequence {
            api.getTasks()
            dao.deleteAllTasks()
            dao.insertTasks(any())
        }
    }

}