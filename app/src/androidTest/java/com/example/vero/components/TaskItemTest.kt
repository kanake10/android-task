package com.example.vero.components

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.example.vero.testTask
import com.example.vero.ui.components.TaskItem
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class TaskItemTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun taskItemDisplaysCorrectly() {


        composeTestRule.setContent {
            TaskItem(task = testTask)
        }
        composeTestRule.onNodeWithText("100 Aufbau").assertIsDisplayed()
        composeTestRule.onNodeWithText("Gerüst montieren").assertIsDisplayed()
        composeTestRule.onNodeWithText("Gerüste montieren.").assertIsDisplayed()
    }
}

