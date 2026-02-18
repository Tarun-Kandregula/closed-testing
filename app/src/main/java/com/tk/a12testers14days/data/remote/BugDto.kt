package com.tk.a12testers14days.data.remote

data class BugDto(
    val _id: String,
    val appId: String,
    val testerId: UserDto?, // Expected UserDto object
    val title: String,
    val description: String?,
    val status: String, // 'open', 'resolved'
    val developerReply: String?,
    val createdAt: String,
    val chat: List<ChatMessageDto>? = emptyList()
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
    val message: String
)
