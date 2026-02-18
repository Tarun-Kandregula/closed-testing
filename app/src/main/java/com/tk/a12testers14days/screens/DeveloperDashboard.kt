package com.tk.a12testers14days.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tk.a12testers14days.DeveloperViewModel
import com.tk.a12testers14days.data.remote.AppDto
import org.koin.androidx.compose.koinViewModel

@Composable
fun DeveloperDashboard(
    onAddApp: () -> Unit,
    onAppClick: (AppDto) -> Unit,
    onEditApp: (AppDto) -> Unit,
    viewModel: DeveloperViewModel = koinViewModel()
) {
    val myApps by viewModel.myApps.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadMyApps()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            if (isLoading) {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            } else if (myApps.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("No apps submitted yet", color = Color.Gray)
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = onAddApp) {
                            Text("Submit New App")
                        }
                    }
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(myApps) { app ->
                        AppItemCard(
                            app = app, 
                            onClick = { onAppClick(app) },
                            onEditClick = { onEditApp(app) }
                        )
                    }
                }
            }
            
            if (error != null) {
                Text(error!!, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 8.dp))
            }
        }

        FloatingActionButton(
            onClick = onAddApp,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp),
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = Color.White
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add App")
        }
    }
}

@Composable
fun AppItemCard(app: AppDto, onClick: () -> Unit, onEditClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = app.appName,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = app.packageName ?: "No package name",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    StatusBadge(app.status)
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Testers: ${app.optedInTesters?.size ?: 0} / ${app.maxTesters ?: 20}",
                    fontSize = 14.sp,
                    color = Color.LightGray
                )
                
                Text(
                    text = "₹${app.paymentAmount}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF4ADE80)
                )
            }
        }
    }
}

@Composable
fun StatusBadge(status: String) {
    val (color, text) = when (status) {
        "testing" -> Color(0xFF22C55E) to "TESTING" // Green
        "opt_in_period" -> Color(0xFFEAB308) to "RECRUITING" // Yellow
        "completed" -> Color(0xFFA855F7) to "COMPLETED" // Purple
        else -> Color.Gray to status.uppercase()
    }

    Surface(
        color = color.copy(alpha = 0.2f),
        shape = RoundedCornerShape(4.dp),
    ) {
        Text(
            text = text,
            color = color,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}
