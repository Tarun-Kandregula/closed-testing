package com.tk.a12testers14days

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tk.a12testers14days.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel(private val repository: AuthRepository) : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _loginSuccess = MutableSharedFlow<Unit>()
    val loginSuccess: SharedFlow<Unit> = _loginSuccess

    fun login(email: String, pass: String, role: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            val result = repository.login(email, pass, role)
            _isLoading.value = false
            if (result.isSuccess) {
                _loginSuccess.emit(Unit)
            } else {
                _error.value = result.exceptionOrNull()?.message ?: "Login failed"
            }
        }
    }

    fun signup(email: String, pass: String, role: String, displayName: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            val result = repository.signup(email, pass, role, displayName)
            _isLoading.value = false
            if (result.isSuccess) {
                _loginSuccess.emit(Unit)
            } else {
                _error.value = result.exceptionOrNull()?.message ?: "Signup failed"
            }
        }
    }

    fun clearError() {
        _error.value = null
    }
}
