package com.example.vero.ui.components

import android.content.Intent
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.example.vero.ui.QrScannerActivity
import com.example.vero.ui.viewmodel.TaskState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskScreenContent(
    uiState: TaskState,
    searchQuery: String,
    onSearchQueryChanged: (String) -> Unit,
    onRetry: () -> Unit,
    onQrScanRequested: (String) -> Unit
) {
    val context = LocalContext.current

    val qrScannerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        result.data?.getStringExtra("scanned_code")?.takeIf { it.isNotBlank() }?.let {
            onQrScanRequested(it)
        }
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            val intent = Intent(context, QrScannerActivity::class.java)
            qrScannerLauncher.launch(intent)
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TaskTopBar(
            onQrClick = {
                if (ContextCompat.checkSelfPermission(
                        context, android.Manifest.permission.CAMERA
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    val intent = Intent(context, QrScannerActivity::class.java)
                    qrScannerLauncher.launch(intent)
                } else {
                    cameraPermissionLauncher.launch(android.Manifest.permission.CAMERA)
                }
            }
        )

        SearchBar(
            query = searchQuery,
            onQueryChange = onSearchQueryChanged
        )
        TaskContent(uiState, onRetry)
    }
}
