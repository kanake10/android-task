package com.example.vero.components

import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import com.example.vero.ui.components.TaskContent
import com.example.vero.ui.viewmodel.TaskState
import org.junit.Rule
import org.junit.Test

class LoadingIndicatorTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun loadingIndicatorIsDisplayedWhenLoadingAndTasksEmpty() {
        composeTestRule.setContent {
            // Simulate the condition where loading is true and tasks are empty
            val uiState = remember {
                mutableStateOf(
                    TaskState(
                        isLoading = true,
                        tasks = emptyList()
                    )
                )
            }

            TaskContent(
                uiState = uiState.value,
                onRetry = {}
                )
        }

        composeTestRule
            .onNodeWithTag("LoadingIndicator")
            .assertIsDisplayed()
    }
}

