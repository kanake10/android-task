package com.example.vero.components

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.vero.ui.components.ErrorSection
import org.junit.Rule
import org.junit.Test

class ErrorStateTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun errorStateDisplaysErrorMessageAndRetryButton() {
        var retried = false
        composeTestRule.setContent {
            ErrorSection(
                error = "Something went wrong",
                onRetry = { retried = true }
            )
        }

        composeTestRule.onNodeWithText("Something went wrong").assertIsDisplayed()
        composeTestRule.onNodeWithText("Retry").performClick()
        assert(retried)
    }
}
