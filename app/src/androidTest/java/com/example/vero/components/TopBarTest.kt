package com.example.vero.components

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import com.example.vero.ui.components.TaskTopBar
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalMaterial3Api::class)
class TopBarTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun topBarDisplaysTitleAndIcon() {
        composeTestRule.setContent {
            TaskTopBar(
                onQrClick = {},
                )
        }
        composeTestRule.onNodeWithText("Task App").assertIsDisplayed()
        composeTestRule.onNodeWithContentDescription("Scan QR Code").assertIsDisplayed()
    }
}
