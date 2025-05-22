package com.example.vero.db

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import app.cash.turbine.test
import com.example.vero.db.dao.TaskDao
import com.example.vero.helpers.firstTaskModel
import com.example.vero.helpers.secondTaskModel

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class TaskDaoTest {

    private lateinit var database: TaskDatabase
    private lateinit var dao: TaskDao

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            TaskDatabase::class.java
        ).allowMainThreadQueries() // Only for testing purposes
            .build()
        dao = database.taskDao()
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun insertAndRetrieveSampleTask() = runTest {
        dao.insertTasks(listOf(firstTaskModel))

        val tasks = dao.getAllTasks().first()
        assertEquals(1, tasks.size)
        val task = tasks[0]
        assertEquals(firstTaskModel.task, task.task)
        assertEquals(firstTaskModel.title, task.title)
        assertEquals(firstTaskModel.description, task.description)
        assertEquals(firstTaskModel.isAvailableInTimeTrackingKioskMode, task.isAvailableInTimeTrackingKioskMode)
    }

    @Test
    fun insertMultipleTasksAndGetAll() = runTest {
        dao.insertTasks(listOf(firstTaskModel, secondTaskModel))

        val tasks = dao.getAllTasks().first()
        assertEquals(2, tasks.size)
        assertTrue(tasks.any { it.task == firstTaskModel.task && it.title == firstTaskModel.title })
        assertTrue(tasks.any { it.task == secondTaskModel.task && it.title == secondTaskModel.title })
    }

    @Test
    fun deleteAllTasksClearsDatabase() = runTest {
        dao.insertTasks(listOf(firstTaskModel, secondTaskModel))

        dao.deleteAllTasks()

        val tasks = dao.getAllTasks().first()
        assertTrue(tasks.isEmpty())
    }

    @Test
    fun searchTasksReturnsMatchingResults() = runTest {
        dao.insertTasks(listOf(firstTaskModel, secondTaskModel))
        // Search by "Aufbau" should match sampleTask's task and wageType from our constant
        dao.searchTasks("Aufbau").test {
            val results = awaitItem()
            assertTrue(results.any { it.task == firstTaskModel.task })
            cancelAndIgnoreRemainingEvents()
        }
        // Search by "abbauen" for example should match sampleTask2's description and title from our constant
        dao.searchTasks("abbauen").test {
            val results = awaitItem()
            assertTrue(results.any { it.task == secondTaskModel.task })
            cancelAndIgnoreRemainingEvents()
        }
        // Search by something not present should return empty list
        dao.searchTasks("nonexistent").test {
            val results = awaitItem()
            assertTrue(results.isEmpty())
            cancelAndIgnoreRemainingEvents()
        }
    }
}
