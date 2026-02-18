package com.tk.a12testers14days.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.tk.a12testers14days.data.remote.ApiService
import com.tk.a12testers14days.data.remote.LoginRequest
import com.tk.a12testers14days.data.remote.LoginResponse
import com.tk.a12testers14days.data.remote.SignupRequest
import com.tk.a12testers14days.data.remote.UserDto

class AuthRepository(
    private val api: ApiService,
    context: Context
) {
    private val prefs: SharedPreferences = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    suspend fun login(email: String, password: String, role: String): Result<LoginResponse> {
        return try {
            val response = api.login(LoginRequest(email, password, role))
            saveAuthData(response)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signup(email: String, password: String, role: String, displayName: String): Result<LoginResponse> {
        return try {
            val response = api.signup(SignupRequest(email, password, role, displayName))
            saveAuthData(response)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun saveAuthData(response: LoginResponse) {
        prefs.edit()
            .putString("token", response.token)
            .putString("user", gson.toJson(response.user))
            .apply()
    }

    fun getToken(): String? {
        return prefs.getString("token", null)
    }

    fun getUser(): UserDto? {
        val json = prefs.getString("user", null) ?: return null
        return gson.fromJson(json, UserDto::class.java)
    }

    fun logout() {
        prefs.edit().clear().apply()
    }

    fun isLoggedIn(): Boolean {
        return getToken() != null
    }
}
