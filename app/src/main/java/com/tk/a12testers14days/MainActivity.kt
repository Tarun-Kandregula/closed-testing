package com.tk.a12testers14days

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
fun TwelveTestersTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = darkColorScheme(
            primary = Color(0xFF8B5CF6),
            secondary = Color(0xFF06B6D4),
            background = Color(0xFF020617),
            surface = Color(0xFF1E293B)
        ),
        content = content
    )
}

@Composable
fun MainScreen() {
    var role by remember { mutableStateOf<String?>(null) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0xFF020617), Color(0xFF0F172A))
                    )
                )
        ) {
            Header()
            
            if (role == null) {
                RoleSelection { role = it }
            } else {
                Dashboard(role!!)
            }
        }
    }
}

@Composable
fun Header() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "12Testers",
            fontSize = 24.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color.White
        )
    }
}

@Composable
fun RoleSelection(onSelect: (String) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Welcome back",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "Select your role to continue",
            color = Color.Gray
        )
        Spacer(modifier = Modifier.height(48.dp))
        
        RoleCard("I am a Developer", "Get your app to production", Color(0xFF8B5CF6)) {
            onSelect("developer")
        }
        Spacer(modifier = Modifier.height(16.dp))
        RoleCard("I am a Tester", "Test apps and earn rewards", Color(0xFF06B6D4)) {
            onSelect("tester")
        }
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

@Composable
fun Dashboard(role: String) {
    Column(modifier = Modifier.padding(24.dp)) {
        Text(
            if (role == "developer") "Developer Dashboard" else "Tester Portal",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Spacer(modifier = Modifier.height(24.dp))
        
        if (role == "tester") {
            Text("Available Missions", fontWeight = FontWeight.Bold, color = Color.Gray)
            Spacer(modifier = Modifier.height(16.dp))
            MissionItem("Cyber Runner", "₹25.00")
            MissionItem("Eco Tracker", "₹25.00")
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(Color(0xFF1E293B), RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text("No apps submitted yet", color = Color.Gray)
            }
        }
    }
}

@Composable
fun MissionItem(name: String, reward: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B))
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(name, fontWeight = FontWeight.Bold, color = Color.White)
                Text(reward, color = Color(0xFF10B981))
            }
            Button(onClick = { }) {
                Text("Start")
            }
        }
    }
}
