package com.tk.a12testers14days

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.activity.compose.BackHandler

import com.tk.a12testers14days.screens.AuthScreen
import com.tk.a12testers14days.screens.DeveloperDashboard
import com.tk.a12testers14days.screens.MyTestsScreen
import com.tk.a12testers14days.screens.BrowseAppsScreen
import com.tk.a12testers14days.screens.AppDetailScreen
import com.tk.a12testers14days.screens.AddAppScreen
import com.tk.a12testers14days.screens.AnalyticsScreen
import com.tk.a12testers14days.screens.WalletScreen
import com.tk.a12testers14days.screens.StatsScreen
import com.tk.a12testers14days.screens.ReportBugScreen
import com.tk.a12testers14days.screens.MyBugsScreen
import org.koin.androidx.compose.koinViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        setContent {
            TwelveTestersTheme {
                MainScreen()
            }
        }
    }
}

@Composable
fun TwelveTestersTheme(
    darkTheme: Boolean = androidx.compose.foundation.isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val darkColorScheme = darkColorScheme(
        primary = Color(0xFF3B82F6), // Blue
        secondary = Color(0xFF06B6D4), // Cyan
        background = Color(0xFF020617),
        surface = Color(0xFF1E293B),
        onPrimary = Color.White,
        onBackground = Color.White,
        onSurface = Color.White
    )

    val lightColorScheme = lightColorScheme(
        primary = Color(0xFF3B82F6), // Blue
        secondary = Color(0xFF06B6D4), // Cyan
        background = Color(0xFFF8FAFC), // Off-white (Slate-50)
        surface = Color(0xFFFFFFFF), // White
        onPrimary = Color.White,
        onBackground = Color(0xFF0F172A), // Dark Slate
        onSurface = Color(0xFF1E293B) // Slate-800
    )

    MaterialTheme(
        colorScheme = if (darkTheme) darkColorScheme else lightColorScheme,
        content = content
    )
}

@Composable
fun MainScreen(
    viewModel: MainViewModel = koinViewModel()
) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val userRole by viewModel.userRole.collectAsState()
    val missions by viewModel.missions.collectAsState()

    val currentTab by viewModel.currentTab.collectAsState()

    val appToEdit by viewModel.appToEdit.collectAsState()
    val toastMessage by viewModel.toastMessage.collectAsState()
    val context = androidx.compose.ui.platform.LocalContext.current

    LaunchedEffect(toastMessage) {
        toastMessage?.let {
            android.widget.Toast.makeText(context, it, android.widget.Toast.LENGTH_LONG).show()
            viewModel.clearToastMessage()
        }
    }

    val developerTabs = listOf(
        BottomNavItem("Apps", Icons.Default.Home),
        BottomNavItem("Add", Icons.Default.Add),
        BottomNavItem("Analytics", Icons.Default.DateRange),
        BottomNavItem("Wallet", Icons.Default.ShoppingCart)
    )

    val testerTabs = listOf(
        BottomNavItem("Tests", Icons.Default.CheckCircle),
        BottomNavItem("Browse", Icons.Default.Search),
        BottomNavItem("Stats", Icons.Default.Star),
        BottomNavItem("Wallet", Icons.Default.ShoppingCart)
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            // Determine Title based on Screen and Tab
            var title = "Closed testing community"
            val isDashboard = currentScreen == Screen.Dashboard && userRole != null
            
            if (isDashboard) {
                title = if (userRole == "developer") {
                    when (currentTab) {
                        0 -> "My Apps"
                        1 -> if (appToEdit != null) "Edit App" else "Add New App"
                        2 -> "App Analytics"
                        3 -> "My Wallet"
                        else -> "Closed testing community"
                    }
                } else {
                     when (currentTab) {
                        0 -> "My Tests"
                        1 -> "Browse Apps"
                        2 -> "App Stats"
                        3 -> "My Wallet"
                        else -> "Closed testing community"
                    }
                }
            } else if (currentScreen == Screen.AddApp) {
                title = "Add New App"
            } else if (currentScreen == Screen.ReportBug) {
                val appName by viewModel.bugReportAppName.collectAsState()
                title = "Report Bug: ${appName ?: ""}"
            } else if (currentScreen == Screen.BugDetails) {
                title = "Bug Details"
            }

            // Hide Main Bar on AppDetails as it has its own
            val isMainBarVisible = currentScreen != Screen.AppDetails
            
            if (isMainBarVisible) {
                @OptIn(ExperimentalMaterial3Api::class)
                TopAppBar(
                    title = {
                        val logo = if (androidx.compose.foundation.isSystemInDarkTheme()) 
                            R.drawable.logo_white 
                        else 
                            R.drawable.logo_black
                        
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            androidx.compose.foundation.Image(
                                painter = androidx.compose.ui.res.painterResource(id = logo),
                                contentDescription = "Closed testing community",
                                modifier = Modifier.height(36.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = title,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1,
                                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                            )
                        }
                    },
                    actions = {
                        if (currentScreen == Screen.Dashboard) {
                            if (userRole == "tester") {
                                // Removed My Bugs shortcut as requested
                            }
                            TextButton(onClick = { viewModel.logout() }) {
                                Text("Logout", color = MaterialTheme.colorScheme.primary)
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        titleContentColor = MaterialTheme.colorScheme.onSurface,
                        actionIconContentColor = MaterialTheme.colorScheme.primary
                    )
                )
            }
        },
        bottomBar = {
            if (currentScreen == Screen.Dashboard && userRole != null) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface
                ) {
                    val tabs = if (userRole == "developer") developerTabs else testerTabs
                    tabs.forEachIndexed { index, item ->
                        NavigationBarItem(
                            icon = { Icon(item.icon, contentDescription = item.name) },
                            label = { Text(item.name) },
                            selected = currentTab == index,
                            onClick = { viewModel.selectTab(index) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                indicatorColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp),
                                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            when (currentScreen) {
                Screen.Landing -> RoleSelectionScreen(
                    onSelectDeveloper = { viewModel.selectRole("developer") },
                    onSelectTester = { viewModel.selectRole("tester") }
                )
                Screen.Login -> userRole?.let { role ->
                    AuthScreen(
                        selectedRole = role,
                        onLoginSuccess = { viewModel.loginSuccess() }
                    )
                }
                Screen.Dashboard -> {
                    // Handle back press to return to home tab if not on first tab
                    BackHandler(enabled = currentTab != 0) {
                        viewModel.selectTab(0)
                    }

                    when (userRole) {
                        "tester" -> {
                            val testerVm: com.tk.a12testers14days.TesterViewModel = koinViewModel()
                             when (currentTab) {
                                 0 -> MyTestsScreen(
                                     onAppClick = { app -> viewModel.navigateToAppDetails(app) },
                                     onReportBug = { appId, appName -> viewModel.navigateToReportBug(appId, appName) },
                                     viewModel = testerVm
                                 )
                                 1 -> BrowseAppsScreen(
                                     onAppClick = { app -> viewModel.navigateToAppDetails(app) },
                                     viewModel = testerVm
                                 )
                                 2 -> StatsScreen()
                                 3 -> WalletScreen()
                             }
                        }
                        "developer" -> {
                            when (currentTab) {
                                0 -> DeveloperDashboard(
                                    onAddApp = { viewModel.selectTab(1) }, // Switch to Add tab
                                    onAppClick = { app -> 
                                        viewModel.navigateToAppDetails(app)
                                    },
                                    onEditApp = { app -> 
                                        viewModel.navigateToEditApp(app)
                                    }
                                )
                                1 -> AddAppScreen(
                                    onBack = { viewModel.selectTab(0) }, // Go back to Apps
                                    onSuccess = { viewModel.selectTab(0) }, // Go back to Apps on success
                                    isEmbedded = true,
                                    appToEdit = appToEdit
                                )
                                2 -> AnalyticsScreen()
                                3 -> WalletScreen()
                            }
                        }
                        else -> Text("Error: Unknown Role", color = Color.Red, modifier = Modifier.padding(16.dp))
                    }
                }
                Screen.AddApp -> {
                    // Legacy separate screen support if needed, but we use tab now
                    // Keep it just in case logic pushes Screen.AddApp
                    AddAppScreen(
                        onBack = { viewModel.navigateBack() },
                        onSuccess = { viewModel.navigateBack() }
                    )
                }
                Screen.AppDetails -> {
                    BackHandler { viewModel.navigateBack() }
                    
                    val selectedApp by viewModel.selectedApp.collectAsState()
                    selectedApp?.let { app ->
                        val testerVm: com.tk.a12testers14days.TesterViewModel = koinViewModel()
                        val developerVm: com.tk.a12testers14days.DeveloperViewModel = koinViewModel()
                        val bugs by viewModel.appBugs.collectAsState()
                        
                        com.tk.a12testers14days.screens.AppDetailScreen(
                            app = app,
                            userRole = userRole ?: "",
                            onBack = { viewModel.navigateBack() },
                            onPrimaryAction = {
                                if (userRole == "tester") {
                                    testerVm.joinTest(app.id)
                                    viewModel.navigateBack() 
                                } else if (userRole == "developer") {
                                    developerVm.startTesting(app.id)
                                    viewModel.navigateBack()
                                }
                            },
                            onEditClick = {
                                viewModel.navigateToEditApp(app)
                            },
                            bugs = bugs,
                            onBugClick = { bug -> viewModel.navigateToBugDetails(bug) }
                        )
                    }
                }
                Screen.BugDetails -> {
                    BackHandler { viewModel.navigateBack() }
                    val selectedBug by viewModel.selectedBug.collectAsState()
                    selectedBug?.let { bug ->
                        val chatInput by viewModel.chatInput.collectAsState()
                        com.tk.a12testers14days.screens.BugDetailScreen(
                            bug = bug,
                            chatInput = chatInput,
                            onChatInputChange = { viewModel.updateChatInput(it) },
                            onSendChat = { viewModel.sendChat() },
                            currentUserRole = userRole
                        )
                    }
                }
                Screen.ReportBug -> {
                    val appId by viewModel.bugReportAppId.collectAsState()
                    val appName by viewModel.bugReportAppName.collectAsState()

                    if (appId != null && appName != null) {
                        ReportBugScreen(
                            appId = appId!!,
                            appName = appName!!,
                            onBackClick = { viewModel.navigateBack() }
                        )
                    } else {
                        viewModel.navigateBack()
                    }
                }
                Screen.MyBugs -> {
                    MyBugsScreen()
                    BackHandler { viewModel.navigateBack() }
                }
            }
        }
    }
}

data class BottomNavItem(val name: String, val icon: ImageVector)



@Composable
fun RoleSelectionScreen(onSelectDeveloper: () -> Unit, onSelectTester: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Welcome",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "Select your role to continue",
            color = Color.Gray
        )
        Spacer(modifier = Modifier.height(48.dp))
        
        RoleCard("I am a Developer", "Get your app to production", Color(0xFF8B5CF6), onSelectDeveloper)
        Spacer(modifier = Modifier.height(16.dp))
        RoleCard("I am a Tester", "Test apps and earn rewards", Color(0xFF06B6D4), onSelectTester)
    }
}

@Composable
fun RoleCard(title: String, sub: String, color: Color, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(title, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = color)
            Text(sub, fontSize = 14.sp, color = Color.Gray)
        }
    }
}


