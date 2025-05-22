package com.example.vero.components

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTextInput
import com.example.vero.ui.components.SearchBar
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalTestApi::class)
class SearchBarTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun searchBarDisplaysPlaceholderAndUpdatesQuery() {
        var query = ""

        composeTestRule.setContent {
            SearchBar(
                query = query,
                onQueryChange = { query = it }
            )
        }
        // Verify placeholder
        composeTestRule
            .onNodeWithText("Search Tasks Here...")
            .assertIsDisplayed()

        composeTestRule
            .onNode(hasSetTextAction())
            .performTextInput("Meeting")

        // Allow recomposition
        composeTestRule.waitForIdle()

        assert(query == "Meeting") { "Expected query to be 'Meeting', but was '$query'" }
    }
}