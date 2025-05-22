package com.example.vero.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.vero.ui.viewmodel.TaskState
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState

@Composable
fun TaskContent(uiState: TaskState, onRetry: () -> Unit) {
    when {
        uiState.isLoading && uiState.tasks.isEmpty() -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .wrapContentSize(Alignment.Center)
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.testTag("LoadingIndicator")
                )
            }
        }

        uiState.error != null -> {
            ErrorSection(uiState.error, onRetry)
        }

        uiState.tasks.isEmpty() -> {
            Text(
                "No tasks found",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                textAlign = TextAlign.Center
            )
        }

        else -> {
            SwipeRefresh(
                state = rememberSwipeRefreshState(isRefreshing = uiState.isLoading),
                onRefresh = onRetry
            ) {
                LazyColumn(modifier = Modifier.padding(horizontal = 16.dp)) {
                    items(uiState.tasks) { task ->
                        TaskItem(task)
                    }
                }
            }
        }
    }
}
