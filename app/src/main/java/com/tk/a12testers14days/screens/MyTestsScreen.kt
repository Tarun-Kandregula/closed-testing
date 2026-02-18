package com.tk.a12testers14days.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tk.a12testers14days.TesterViewModel
import com.tk.a12testers14days.data.remote.AppDto
import org.koin.androidx.compose.koinViewModel

@Composable
fun MyTestsScreen(
    onAppClick: (AppDto) -> Unit,
    onReportBug: (String, String) -> Unit, // appId, appName
    viewModel: TesterViewModel = koinViewModel()
) {
    val myTests by viewModel.myTests.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val currentUserId = viewModel.getCurrentUserId()
    
    val checkInMessage by viewModel.checkInSuccess.collectAsState(initial = null)
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.loadData()
    }
    
    LaunchedEffect(checkInMessage) {
        checkInMessage?.let {
            snackbarHostState.showSnackbar(it)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            Column {
                if (isLoading) {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    }
                } else {
                    AppsList(
                        apps = myTests, 
                        emptyMessage = "No active tests. Browse apps to join.", 
                        onAppClick = onAppClick,
                        currentUserId = currentUserId,
                        onCheckIn = { appId, pkg -> viewModel.checkIn(appId, pkg) },
                        onReportBug = { appId -> 
                            val app = myTests.find { it.id == appId }
                            onReportBug(appId, app?.appName ?: "App")
                        }
                    )
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
}
