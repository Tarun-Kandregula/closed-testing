package com.tk.a12testers14days.data.remote

data class BugDto(
    val _id: String,
    val appId: BugAppDto, // Changed to object to match populated response
    val testerId: UserDto?,
    val title: String,
    val description: String?,
    val status: String,
    val developerReply: String?,
    val createdAt: String,
    val chat: List<ChatMessageDto>? = emptyList()
)

data class BugAppDto(
    val _id: String,
    val appName: String,
    val icon: String?,
    val packageId: String?
)

data class ChatMessageDto(
    val senderId: ChatSenderDto?, 
    val role: String,
    val message: String,
    val timestamp: String
)

data class ChatSenderDto(
    val _id: String?,
    val displayName: String?,
    val role: String?
)

data class CreateBugRequest(
    val appId: String,
    val title: String,
    val description: String
)

data class ChatRequest(
    val message: String,
    val senderRole: String,
    val senderName: String
)
