package com.example.vero.ui


import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.vero.ui.components.TaskScreenContent
import com.example.vero.ui.viewmodel.TaskViewModel

@Composable
fun TaskScreen(viewModel: TaskViewModel = hiltViewModel()) {
    val uiState by viewModel.taskState.collectAsStateWithLifecycle()
    var searchQuery by rememberSaveable { mutableStateOf("") }

    TaskScreenContent(
        uiState = uiState,
        searchQuery = searchQuery,
        onSearchQueryChanged = {
            searchQuery = it
            viewModel.onSearchQueryChanged(it)
        },
        onRetry = viewModel::refreshTasks,
        onQrScanRequested = { viewModel.onSearchQueryChanged(it) }
    )
}
