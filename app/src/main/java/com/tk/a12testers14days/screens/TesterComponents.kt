package com.tk.a12testers14days.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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

// TesterDashboard composable removed as it is replaced by MyTestsScreen and BrowseAppsScreen
// Shared components AppsList and TesterAppCard are retained here.



@Composable
fun AppsList(
    apps: List<AppDto>, 
    emptyMessage: String, 
    onAppClick: (AppDto) -> Unit,
    currentUserId: String? = null,
    onCheckIn: ((String, String) -> Unit)? = null,
    onReportBug: ((String) -> Unit)? = null
) {
    if (apps.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(emptyMessage, color = Color.Gray)
        }
    } else {
        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(apps) { app ->
                if (currentUserId != null && onCheckIn != null) {
                    MyTestItemCard(
                        app, 
                        currentUserId, 
                        onCheckIn, 
                        onReportBug = { onReportBug?.invoke(app.id) },
                        onClick = { onAppClick(app) }
                    )
                } else {
                    TesterAppCard(app, onClick = { onAppClick(app) })
                }
            }
        }
    }
}

@Composable
fun TesterAppCard(app: AppDto, onClick: () -> Unit) {
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
                Text(
                    text = app.appName,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                // Reuse existing StatusBadge or simple text
                StatusBadge(app.status)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Reward: ₹${app.paymentAmount}",
                fontSize = 14.sp,
                color = Color(0xFF4ADE80) // Green
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = app.appDescription ?: "No description",
                fontSize = 14.sp,
                color = Color.Gray,
                maxLines = 2
            )
        }
    }
}

@Composable
fun MyTestItemCard(
    app: AppDto, 
    currentUserId: String?, 
    onCheckIn: (String, String) -> Unit, // appId, packageName
    onReportBug: () -> Unit,
    onClick: () -> Unit
) {
    val testerInfo = app.optedInTesters?.find { it.testerId?._id == currentUserId }
    val daysCompleted = testerInfo?.daysCompleted ?: 0
    val duration = app.durationDays ?: 15
    val progress = (daysCompleted.toFloat() / duration.toFloat()).coerceIn(0f, 1f)
    
    val isTesting = app.status == "testing"
    val isCompleted = testerInfo?.status == "completed"
    
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
                Column {
                    Text(
                        text = app.appName,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = app.packageName ?: "",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
                StatusBadge(if (isCompleted) "completed" else app.status)
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Progress Bar
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Day $daysCompleted / $duration",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "${(progress * 100).toInt()}%",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth().height(8.dp).background(Color.LightGray.copy(alpha=0.3f), RoundedCornerShape(4.dp)),
                color = MaterialTheme.colorScheme.primary,
                trackColor = Color.Transparent,
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { 
                        if (app.packageName != null) {
                            onCheckIn(app.id, app.packageName) 
                        }
                    },
                    modifier = Modifier.weight(1f).height(40.dp),
                    enabled = isTesting && !isCompleted,
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isCompleted) Color(0xFFA855F7) else MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text(
                         if (isCompleted) "Completed" 
                         else if (!isTesting) "Waiting"
                         else "Check In" 
                    )
                }
                
                if (isTesting) {
                    OutlinedButton(
                        onClick = onReportBug,
                        modifier = Modifier.height(40.dp),
                        shape = RoundedCornerShape(50)
                    ) {
                        Text("Report Bug")
                    }
                }
            }
        }
    }
}
