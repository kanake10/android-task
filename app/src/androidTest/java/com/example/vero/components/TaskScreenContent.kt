package com.example.vero.components

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.example.vero.ui.components.TaskScreenContent
import com.example.vero.ui.viewmodel.TaskState
import org.junit.Rule
import org.junit.Test

class TaskScreenContent {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun showsNoTasksFoundTextWhenTaskListIsEmpty() {
        composeTestRule.setContent {
            TaskScreenContent(
                uiState = TaskState(isLoading = false, tasks = emptyList()),
                searchQuery = "",
                onSearchQueryChanged = {},
                onRetry = {},
                onQrScanRequested = {}
            )
        }

        composeTestRule.onNodeWithText("No tasks found").assertIsDisplayed()

    }
}

