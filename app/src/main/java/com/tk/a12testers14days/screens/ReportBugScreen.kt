package com.tk.a12testers14days.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tk.a12testers14days.TesterViewModel
import kotlinx.coroutines.flow.collectLatest
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportBugScreen(
    appId: String,
    appName: String,
    onBackClick: () -> Unit,
    viewModel: TesterViewModel = koinViewModel()
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.bugReportSuccess.collectLatest {
            snackbarHostState.showSnackbar("Bug reported successfully!")
            onBackClick()
        }
    }

    LaunchedEffect(error) {
        if (error != null) {
            snackbarHostState.showSnackbar(error ?: "Unknown error")
            viewModel.clearError()
        }
    }

    Scaffold(
        // topBar handled by MainActivity
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                "Describe the issue",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Bug Title") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description & Steps to Reproduce") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                maxLines = 10
            )

            Button(
                onClick = { viewModel.reportBug(appId, title, description) },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                enabled = !isLoading && title.isNotBlank() && description.isNotBlank()
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Submit Bug Report")
                }
            }
        }
    }
}
