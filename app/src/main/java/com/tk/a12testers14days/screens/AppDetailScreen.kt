package com.tk.a12testers14days.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tk.a12testers14days.R
import com.tk.a12testers14days.data.remote.AppDto
import com.tk.a12testers14days.MainActivity // Assuming we might need navigation but we use onPrimaryAction

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppDetailScreen(
    app: AppDto,
    userRole: String, // "developer" or "tester"
    onBack: () -> Unit,
    onPrimaryAction: () -> Unit = {},
    onEditClick: () -> Unit = {},
    bugs: List<com.tk.a12testers14days.data.remote.BugDto> = emptyList(),
    onBugClick: (com.tk.a12testers14days.data.remote.BugDto) -> Unit = {}
) {
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)
                        ),
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .clip(CircleShape)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    navigationIconContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            // Header with Gradient
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                                MaterialTheme.colorScheme.background
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    // App Icon Placeholder
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        val logo = if (isSystemInDarkTheme())
                            R.drawable.logo_white
                        else
                            R.drawable.logo_black

                        androidx.compose.foundation.Image(
                            painter = androidx.compose.ui.res.painterResource(id = logo),
                            contentDescription = null,
                            modifier = Modifier.size(54.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = app.appName,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = app.packageName ?: "",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
            }

            Column(modifier = Modifier.padding(horizontal = 20.dp)) {

                // Status Section
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Campaign Status",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    StatusBadge(app.status)
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Stats Grid
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatTile(
                        label = "Testers",
                        value = "${app.optedInTesters?.size ?: 0}/${app.maxTesters ?: 20}",
                        icon = Icons.Default.Person,
                        modifier = Modifier.weight(1f)
                    )
                    StatTile(
                        label = "Payout",
                        value = "₹${app.paymentAmount}",
                        icon = Icons.Default.ShoppingCart, // Closest to rupee/price
                        modifier = Modifier.weight(1f)
                    )
                    StatTile(
                        label = "Duration",
                        value = "${app.durationDays ?: 15} Days",
                        icon = Icons.Default.DateRange,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Info Section
                Text(
                    "Description",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = app.appDescription ?: "No description provided for this application.",
                    color = Color.Gray,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Role Specific Views
                if (app.status == "testing") {
                    TestingInfoView(app)
                } else if (app.status == "opt_in_period") {
                    RecruitmentView(app, userRole)
                }

                Spacer(modifier = Modifier.height(40.dp))

                // Action Buttons
                val isDev = userRole == "developer"
                val isOptIn = app.status == "opt_in_period"

                if (isOptIn) {
                    if (isDev) {
                        Button(
                            onClick = onPrimaryAction,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = RoundedCornerShape(16.dp),
                            enabled = (app.optedInTesters?.size ?: 0) >= (app.maxTesters ?: 20)
                        ) {
                            Text(
                                "Start Testing Period",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedButton(
                            onClick = onEditClick,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = RoundedCornerShape(16.dp),
                            border = ButtonDefaults.outlinedButtonBorder.copy(width = 1.dp)
                        ) {
                            Text("Edit App Details", fontSize = 16.sp)
                        }
                    } else {
                        Button(
                            onClick = onPrimaryAction,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text(
                                "Join Test Campaign",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }


                }

                if (isDev) {
                    TesterListSection(
                        testers = app.optedInTesters ?: emptyList(),
                        testerDetails = app.currentTesters ?: emptyList()
                    )
                }

                // Bug Reports Section
                if (bugs.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        "Reported Bugs",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    bugs.forEach { bug ->
                        BugItem(bug, onClick = { onBugClick(bug) })
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

@Composable
fun BugItem(bug: com.tk.a12testers14days.data.remote.BugDto, onClick: () -> Unit = {}) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(
                alpha = 0.5f
            )
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = bug.title,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = bug.status.uppercase(),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (bug.status == "resolved") Color(0xFF22C55E) else Color(
                        0xFFEF4444
                    ),
                    modifier = Modifier
                        .background(
                            if (bug.status == "resolved") Color(0xFF22C55E).copy(alpha = 0.1f) else Color(
                                0xFFEF4444
                            ).copy(alpha = 0.1f),
                            RoundedCornerShape(4.dp)
                        )
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
            if (bug.testerId != null) {
                Text(
                    text = "Reported by: ${bug.testerId.displayName ?: "Unknown"}",
                    fontSize = 11.sp,
                    color = Color.Gray
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = bug.description ?: "No description provided",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (!bug.developerReply.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .background(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                            RoundedCornerShape(8.dp)
                        )
                        .padding(8.dp)
                ) {
                    Icon(
                        Icons.Default.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            "Developer Reply",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            bug.developerReply ?: "",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StatTile(label: String, value: String, icon: ImageVector, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface.copy(
                alpha = 0.5f
            )
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                value,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(label, fontSize = 11.sp, color = Color.Gray)
        }
    }
}

@Composable
fun RecruitmentView(app: AppDto, role: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary.copy(
                alpha = 0.05f
            )
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Info,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (role == "developer") "Next Steps" else "How to Join",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = if (role == "developer")
                    "Once you reach ${app.maxTesters ?: 20} testers, you can start the 14-day testing period. You currently have ${app.optedInTesters?.size ?: 0} joined."
                else
                    "Join this campaign to help the developer. You'll need to install the app and check-in daily for 15 days to earn your reward.",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun TestingInfoView(app: AppDto) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF22C55E).copy(alpha = 0.05f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "Testing in Progress",
                fontWeight = FontWeight.Bold,
                color = Color(0xFF22C55E)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "The 15-day testing window is active. Testers are currently performing daily check-ins.",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }

}

@Composable
fun TesterListSection(
    testers: List<com.tk.a12testers14days.data.remote.OptedInTesterDto>,
    testerDetails: List<com.tk.a12testers14days.data.remote.TesterDetailDto> = emptyList()
) {
    if (testers.isNotEmpty()) {
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            "Joined Testers (${testers.size})",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(12.dp))

        testers.forEach { tester ->
            // Find device details if available
            val detail = testerDetails.find { it._id == tester.testerId?._id }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(
                        alpha = 0.5f
                    )
                )
            ) {
                Row(
                    modifier = Modifier
                        .padding(12.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = tester.testerId?.displayName ?: "Unknown Tester",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = tester.testerId?.email ?: "",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )

                        if (detail != null) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Device: ${detail.deviceModel ?: "Unknown"} (Android ${detail.androidVersion ?: "?"})",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "${tester.daysCompleted} / 14 Days",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = tester.status?.uppercase() ?: "UNKNOWN",
                            fontSize = 10.sp,
                            color = if (tester.status == "active") Color(0xFF22C55E) else Color.Gray
                        )
                    }
                }
            }
        }
    } else {
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            "No testers have joined yet.",
            color = Color.Gray,
            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
        )
    }
}

