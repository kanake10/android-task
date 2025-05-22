package com.example.vero.viewmodel

import com.example.vero.helpers.FakeTaskRepository
import com.example.vero.ui.viewmodel.TaskViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test


@OptIn(ExperimentalCoroutinesApi::class)
class TaskViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: FakeTaskRepository
    private lateinit var viewModel: TaskViewModel

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        repository = FakeTaskRepository()
        viewModel = TaskViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun initLoadsTasksSuccessfully() = runTest {
        // Initialization calls loadTasks automatically
        advanceUntilIdle()

        val state = viewModel.taskState.value
        assertFalse(state.isLoading)
        assertEquals(2, state.tasks.size)
        assertNull(state.error)
    }

    @Test
    fun loadTasksUpdatesStateWithTasks() = runTest {
        viewModel.loadTasks()
        advanceUntilIdle()

        val state = viewModel.taskState.value
        assertFalse(state.isLoading)
        assertEquals(2, state.tasks.size)
        assertNull(state.error)
    }

    @Test
    fun refreshTasksReloadsTasks() = runTest {
        // Modify fake repo data to verify refresh actually reloads
        repository.refreshCount = 0

        viewModel.refreshTasks()
        advanceUntilIdle()
        // refreshTasks should call refreshTasks() in repo
        assertEquals(1, repository.refreshCount)
        val state = viewModel.taskState.value
        assertFalse(state.isLoading)
        assertEquals(2, state.tasks.size)
        assertNull(state.error)
    }

    @Test
    fun searchTasksUpdatesStateWithFilteredResults() = runTest {
        viewModel.onSearchQueryChanged("abbauen")
        advanceUntilIdle()

        val state = viewModel.taskState.value
        assertFalse(state.isLoading)
        assertEquals(1, state.tasks.size)
        assertEquals("Gerüst abbauen", state.tasks.first().title)
        assertNull(state.error)
    }

    @Test
    fun searchTasksWithBlankQueryReloadsAllTasks() = runTest {
        viewModel.onSearchQueryChanged("")
        advanceUntilIdle()

        val state = viewModel.taskState.value
        assertFalse(state.isLoading)
        // returns all tasks because query is blank
        assertEquals(2, state.tasks.size)
        assertNull(state.error)
    }
}

