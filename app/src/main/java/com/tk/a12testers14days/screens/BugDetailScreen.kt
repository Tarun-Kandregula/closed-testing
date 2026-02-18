package com.tk.a12testers14days.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tk.a12testers14days.data.remote.BugDto

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BugDetailScreen(
    bug: BugDto,
    chatInput: String,
    onChatInputChange: (String) -> Unit,
    onSendChat: () -> Unit,
    currentUserRole: String?
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        androidx.compose.foundation.lazy.LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            contentPadding = PaddingValues(top = 20.dp, bottom = 20.dp)
        ) {
            item {
                // Status Badge
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Status",
                        fontSize = 16.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = bug.status.uppercase(),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (bug.status == "resolved") Color(0xFF22C55E) else Color(0xFFEF4444),
                        modifier = Modifier
                            .background(
                                if (bug.status == "resolved") Color(0xFF22C55E).copy(alpha = 0.1f) else Color(0xFFEF4444).copy(alpha = 0.1f),
                                RoundedCornerShape(4.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = bug.title,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.height(8.dp))

                if (bug.testerId != null) {
                    Text(
                        text = "Reported by: ${bug.testerId.displayName ?: "Unknown"}",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Email: ${bug.testerId.email ?: ""}",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                } else {
                    Text(
                        text = "Reported by: Unknown",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Description",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = bug.description ?: "No description provided.",
                    fontSize = 16.sp,
                    lineHeight = 24.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(32.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    "Discussion",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(16.dp))
                
                // Legacy Developer Reply (if exists and no chat)
                if (!bug.developerReply.isNullOrEmpty() && bug.chat.isNullOrEmpty()) {
                    ChatBubble(
                        message = bug.developerReply,
                        senderName = "Developer",
                        isMe = currentUserRole == "developer",
                        role = "developer"
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
            
            // Chat Messages
            if (!bug.chat.isNullOrEmpty()) {
                items(bug.chat.size) { index ->
                    val msg = bug.chat[index]
                    // Determine if it's me based on role matching
                    // Ideally check ID, but role is a decent proxy for now
                    val isMe = msg.role == currentUserRole
                    
                    ChatBubble(
                        message = msg.message,
                        senderName = msg.senderId?.displayName ?: msg.role.replaceFirstChar { if (it.isLowerCase()) it.titlecase(java.util.Locale.getDefault()) else it.toString() },
                        isMe = isMe,
                        role = msg.role
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            } else if (bug.developerReply.isNullOrEmpty()) {
                item {
                     Text("No messages yet.", color = Color.Gray, fontStyle = androidx.compose.ui.text.font.FontStyle.Italic)
                }
            }
        }
        
        // Chat Input
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = chatInput,
                onValueChange = onChatInputChange,
                placeholder = { Text("Type a message...") },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(24.dp),
                maxLines = 3
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = onSendChat,
                enabled = chatInput.isNotBlank(),
                shape = CircleShape,
                contentPadding = PaddingValues(0.dp),
                modifier = Modifier.size(50.dp)
            ) {
                Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send")
            }
        }
    }
}

@Composable
fun ChatBubble(message: String, senderName: String, isMe: Boolean, role: String) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (isMe) Alignment.End else Alignment.Start
    ) {
        Text(
            text = senderName,
            fontSize = 12.sp,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 4.dp, start = 4.dp, end = 4.dp)
        )
        Surface(
            color = if (isMe) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (isMe) 16.dp else 0.dp,
                bottomEnd = if (isMe) 0.dp else 16.dp
            )
        ) {
            Text(
                text = message,
                modifier = Modifier.padding(12.dp),
                color = if (isMe) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
