package com.tk.a12testers14days

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tk.a12testers14days.data.remote.AppDto
import com.tk.a12testers14days.data.remote.BugDto
import com.tk.a12testers14days.data.repository.AppRepository
import com.tk.a12testers14days.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TesterViewModel(
    private val appRepository: AppRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _myTests = MutableStateFlow<List<AppDto>>(emptyList())
    val myTests: StateFlow<List<AppDto>> = _myTests

    private val _availableApps = MutableStateFlow<List<AppDto>>(emptyList())
    val availableApps: StateFlow<List<AppDto>> = _availableApps

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _optInSuccess = MutableSharedFlow<Unit>()
    val optInSuccess: SharedFlow<Unit> = _optInSuccess

    private val _myBugs = MutableStateFlow<List<BugDto>>(emptyList())
    val myBugs: StateFlow<List<BugDto>> = _myBugs

    private val _bugReportSuccess = MutableSharedFlow<Unit>()
    val bugReportSuccess: SharedFlow<Unit> = _bugReportSuccess

    fun loadData() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            val token = authRepository.getToken()
            if (token == null) {
                _error.value = "Not logged in"
                _isLoading.value = false
                return@launch
            }

            // Parallel fetch could be better but sequential is fine for now
            appRepository.getMyTests(token)
                .onSuccess { apps -> _myTests.value = apps }
                .onFailure { e -> _error.value = "Failed to load my tests" }

            appRepository.getAvailableApps(token)
                .onSuccess { apps -> _availableApps.value = apps }
                .onFailure { e -> 
                    // Don't overwrite previous error if any
                    if (_error.value == null) _error.value = "Failed to load available apps" 
                }

            appRepository.getMyBugs(token)
                .onSuccess { bugs -> _myBugs.value = bugs }
                // Failure here is non-critical for main dashboard
            
            _isLoading.value = false
        }
    }

    fun joinTest(appId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            val token = authRepository.getToken()
            if (token == null) {
                _error.value = "Not logged in"
                _isLoading.value = false
                return@launch
            }

            appRepository.optIn(token, appId)
                .onSuccess { response ->
                    if (response.success) {
                        _optInSuccess.emit(Unit)
                        loadData() // Refresh lists
                    } else {
                        _error.value = response.message
                    }
                }
                .onFailure { e ->
                    _error.value = e.message ?: "Failed to join test"
                }
            _isLoading.value = false
        }
    }
    
    private val _checkInSuccess = MutableSharedFlow<String>()
    val checkInSuccess: SharedFlow<String> = _checkInSuccess

    fun getCurrentUserId(): String? {
        return authRepository.getUser()?._id
    }

    fun checkIn(appId: String, packageName: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            val token = authRepository.getToken()
            if (token == null) {
                _error.value = "Not logged in"
                _isLoading.value = false
                return@launch
            }

            appRepository.checkIn(token, appId, packageName)
                .onSuccess { response ->
                    if (response.success) {
                        _checkInSuccess.emit(response.message)
                        loadData() // Refresh data to update progress
                    } else {
                        _error.value = response.message
                    }
                }
                .onFailure { e ->
                    val errorMessage = e.message ?: "Check-in failed"
                    // Handle specific error cases if needed
                    _error.value = errorMessage
                }
            _isLoading.value = false
        }
    }

    fun reportBug(appId: String, title: String, description: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            val token = authRepository.getToken()
            if (token == null) {
                _error.value = "Not logged in"
                _isLoading.value = false
                return@launch
            }

            appRepository.reportBug(token, appId, title, description)
                .onSuccess { 
                    _bugReportSuccess.emit(Unit)
                    // Refresh bugs list
                    loadData() 
                }
                .onFailure { e ->
                    _error.value = e.message ?: "Failed to report bug"
                }
            _isLoading.value = false
        }
    }
    
    fun clearError() {
        _error.value = null
    }
}
