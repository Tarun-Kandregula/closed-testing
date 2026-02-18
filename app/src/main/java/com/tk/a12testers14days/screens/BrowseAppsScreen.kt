package com.tk.a12testers14days.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tk.a12testers14days.TesterViewModel
import com.tk.a12testers14days.data.remote.AppDto
import org.koin.androidx.compose.koinViewModel

@Composable
fun BrowseAppsScreen(
    onAppClick: (AppDto) -> Unit,
    viewModel: TesterViewModel = koinViewModel()
) {
    val availableApps by viewModel.availableApps.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadData()
    }

    Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Column {
            if (isLoading) {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            } else {
                AppsList(availableApps, "No available apps to join", onAppClick)
            }
        }

        if (error != null) {
            Text(
                text = error!!,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}
