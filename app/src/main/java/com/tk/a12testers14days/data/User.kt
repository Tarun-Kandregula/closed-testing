package com.tk.a12testers14days.data

data class User(
    val id: String,
    val email: String,
    val role: String, // "developer" or "tester"
    val displayName: String?,
    val walletBalance: Double = 0.0,
    val phoneNumber: String? = null,
    val country: String? = null,
    val deviceModel: String? = null,
    val androidVersion: String? = null
)

enum class UserRole(val value: String) {
    DEVELOPER("developer"),
    TESTER("tester")
}
