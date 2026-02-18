package com.tk.a12testers14days

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tk.a12testers14days.data.remote.AppDto
import com.tk.a12testers14days.data.remote.SubmitAppRequest
import com.tk.a12testers14days.data.repository.AppRepository
import com.tk.a12testers14days.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DeveloperViewModel(
    private val appRepository: AppRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _myApps = MutableStateFlow<List<AppDto>>(emptyList())
    val myApps: StateFlow<List<AppDto>> = _myApps

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _submissionSuccess = MutableSharedFlow<Unit>()
    val submissionSuccess: SharedFlow<Unit> = _submissionSuccess

    fun loadMyApps() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            val token = authRepository.getToken()
            if (token == null) {
                _error.value = "Not logged in"
                _isLoading.value = false
                return@launch
            }

            appRepository.getMyApps(token)
                .onSuccess { apps ->
                    _myApps.value = apps
                }
                .onFailure { e ->
                    _error.value = e.message ?: "Failed to load apps"
                }
            _isLoading.value = false
        }
    }

    fun submitApp(
        appName: String,
        packageName: String,
        appVersion: String,
        closedTestingLink: String,
        appDescription: String,
        paymentAmount: Int,
        maxTesters: Int,
        durationDays: Int
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            val token = authRepository.getToken()
            if (token == null) {
                _error.value = "Not logged in"
                _isLoading.value = false
                return@launch
            }

            val request = SubmitAppRequest(
                appName, packageName, appVersion, closedTestingLink,
                appDescription, paymentAmount, maxTesters, durationDays
            )

            appRepository.submitApp(token, request)
                .onSuccess { response ->
                    if (response.success) { // ApiService returns SubmitAppResponse with success boolean? Check ApiService.
                        // Actually ApiService.kt defines SubmitAppResponse(val success: Boolean, ...)
                        // Backend returns { message: "...", app: ... } without success: true/false usually?
                        // If parsing fails or success is false, we land here?
                        // Let's assume response.success is usable or true if 200 OK.
                        // Wait, if backend doesn't send "success": true, it might default to false in Kotlin!
                        // I should verify backend response.
                        // Backend: res.status(201).json({ message: '...', app: newApp });
                         // It does NOT send success: true. 
                         // DANGER: `response.success` will be false (default boolean).
                         // I need to change ApiService response to NOT rely on success boolean, or assume success if we get here (Retrofit throws on 4xx/5xx).
                         // BUT `runCatching` catches exceptions.
                         // If I change ApiService to return `AppDto` or a wrapper that matches backend JSON.
                         // Backend JSON: { message: String, app: AppDto }
                         _submissionSuccess.emit(Unit)
                         loadMyApps()
                    } else {
                        // This branch might be taken if success=false, which it will be.
                        // I'll fix this logic implicitly by removing `if (response.success)` since Retrofit success implies http success.
                         _submissionSuccess.emit(Unit)
                         loadMyApps()
                    }
                }
                .onFailure { e ->
                    _error.value = e.message ?: "Submission failed"
                }
            _isLoading.value = false
        }
    }

    fun updateApp(
        appId: String,
        appName: String,
        packageName: String,
        appVersion: String,
        closedTestingLink: String,
        appDescription: String,
        paymentAmount: Int,
        maxTesters: Int,
        durationDays: Int
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            val token = authRepository.getToken()
            if (token == null) {
                _error.value = "Not logged in"
                _isLoading.value = false
                return@launch
            }

            val request = SubmitAppRequest(
                appName, packageName, appVersion, closedTestingLink,
                appDescription, paymentAmount, maxTesters, durationDays
            )

            appRepository.updateApp(token, appId, request)
                .onSuccess {
                     _submissionSuccess.emit(Unit)
                     loadMyApps()
                }
                .onFailure { e ->
                    _error.value = e.message ?: "Update failed"
                }
            _isLoading.value = false
        }
    }

    fun startTesting(appId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            val token = authRepository.getToken()
            if (token == null) {
                _error.value = "Not logged in"
                _isLoading.value = false
                return@launch
            }

            appRepository.startTesting(token, appId)
                .onSuccess { response ->
                    if (response.success) {
                        _submissionSuccess.emit(Unit) // Re-use submission success to trigger navigation/refresh
                        loadMyApps()
                    } else {
                        _error.value = response.message
                    }
                }
                .onFailure { e ->
                    _error.value = e.message ?: "Failed to start testing"
                }
            _isLoading.value = false
        }
    }
    
    fun clearError() {
        _error.value = null
    }
}
