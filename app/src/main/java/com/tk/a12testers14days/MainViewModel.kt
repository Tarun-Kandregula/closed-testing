package com.tk.a12testers14days

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tk.a12testers14days.data.remote.AppDto
import com.tk.a12testers14days.data.repository.AppRepository
import com.tk.a12testers14days.data.repository.AuthRepository
import com.tk.a12testers14days.data.remote.BugDto
import okhttp3.ResponseBody
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class Screen {
    Landing, Login, Dashboard, AddApp, AppDetails, ReportBug, MyBugs, BugDetails
}

class MainViewModel(
    private val authRepository: AuthRepository,
    private val appRepository: AppRepository
) : ViewModel() {

    private val _missions = MutableStateFlow<List<AppDto>>(emptyList())
    val missions: StateFlow<List<AppDto>> = _missions

    private val _userRole = MutableStateFlow<String?>(null)
    val userRole: StateFlow<String?> = _userRole

    private val _currentScreen = MutableStateFlow(Screen.Landing)
    val currentScreen: StateFlow<Screen> = _currentScreen

    fun selectRole(role: String) {
        _userRole.value = role
        _currentScreen.value = Screen.Login
    }

    private val _currentTab = MutableStateFlow(0)
    val currentTab: StateFlow<Int> = _currentTab

    private val _appToEdit = MutableStateFlow<AppDto?>(null)
    val appToEdit: StateFlow<AppDto?> = _appToEdit

    fun selectTab(index: Int) {
        // If selecting Add tab manually, clear edit state to show empty form
        if (index == 1) {
            _appToEdit.value = null
        }
        _currentTab.value = index
    }

    fun navigateToEditApp(app: AppDto) {
        _appToEdit.value = app
        _currentTab.value = 1 // Switch to Add/Edit App tab
        _currentScreen.value = Screen.Dashboard
    }

    fun loginSuccess() {
        _currentScreen.value = Screen.Dashboard
        if (_userRole.value == "tester") {
            refreshMissions()
        }
    }

    fun logout() {
        authRepository.logout()
        _userRole.value = null
        _currentScreen.value = Screen.Landing
        _missions.value = emptyList()
    }

    private val _selectedApp = MutableStateFlow<AppDto?>(null)
    val selectedApp: StateFlow<AppDto?> = _selectedApp

    fun navigateToAddApp() {
        _currentScreen.value = Screen.AddApp
    }

    private val _selectedBug = MutableStateFlow<BugDto?>(null)
    val selectedBug: StateFlow<BugDto?> = _selectedBug

    fun navigateToBugDetails(bug: BugDto) {
        _selectedBug.value = bug
        _currentScreen.value = Screen.BugDetails
    }

    private val _appBugs = MutableStateFlow<List<BugDto>>(emptyList())
    val appBugs: StateFlow<List<BugDto>> = _appBugs

    fun navigateToAppDetails(app: AppDto) {
        _selectedApp.value = app
        _currentScreen.value = Screen.AppDetails
        loadAppBugs(app.id)
    }
    
    fun loadAppBugs(appId: String) {
        viewModelScope.launch {
            val token = authRepository.getToken() ?: return@launch
            appRepository.getAppBugs(token, appId)
                .onSuccess { bugs ->
                    _appBugs.value = bugs
                }
                .onFailure { e ->
                    // Handle error silently, but log it
                    android.util.Log.e("MainViewModel", "Failed to load bugs for app $appId", e)
                    _appBugs.value = emptyList()
                    _toastMessage.value = "Failed to load bugs: ${e.toString()}"
                }
        }
    }

    fun navigateBack() {
        if (_currentScreen.value == Screen.AddApp || _currentScreen.value == Screen.AppDetails || _currentScreen.value == Screen.MyBugs) {
            _currentScreen.value = Screen.Dashboard
            _selectedApp.value = null
        } else if (_currentScreen.value == Screen.ReportBug) {
            // Return to App Details and refresh bugs
            val appId = _bugReportAppId.value
            if (appId != null) {
                // Ensure selected app is still valid or reload it if needed (it should be)
                if (_selectedApp.value == null) {
                    val app = _missions.value.find { it.id == appId }
                    if (app != null) {
                        _selectedApp.value = app
                    }
                }
                _currentScreen.value = Screen.AppDetails
                loadAppBugs(appId)
            } else {
                _currentScreen.value = Screen.Dashboard
            }
            _bugReportAppId.value = null
            _bugReportAppName.value = null
        } else if (_currentScreen.value == Screen.BugDetails) {
            _currentScreen.value = Screen.AppDetails
            _selectedBug.value = null
        } else {
            // Default back behavior or exit app
        }
    }

    fun refreshMissions() {
        viewModelScope.launch {
            val token = authRepository.getToken() ?: return@launch
            // For now, fetch my tests
            appRepository.getMyTests(token).onSuccess { list ->
                _missions.value = list
            }
        }
    }

    private val _bugReportAppId = MutableStateFlow<String?>(null)
    val bugReportAppId: StateFlow<String?> = _bugReportAppId
    
    private val _bugReportAppName = MutableStateFlow<String?>(null)
    val bugReportAppName: StateFlow<String?> = _bugReportAppName

    fun navigateToReportBug(appId: String, appName: String) {
        _bugReportAppId.value = appId
        _bugReportAppName.value = appName
        _currentScreen.value = Screen.ReportBug
    }

    fun navigateToMyBugs() {
        _currentScreen.value = Screen.MyBugs
    }

    private val _chatInput = MutableStateFlow("")
    val chatInput: StateFlow<String> = _chatInput

    fun updateChatInput(text: String) {
        _chatInput.value = text
    }

    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage

    fun clearToastMessage() {
        _toastMessage.value = null
    }

    fun sendChat() {
        val bug = _selectedBug.value ?: return
        val message = _chatInput.value.trim()
        if (message.isBlank()) return

        viewModelScope.launch {
            val token = authRepository.getToken() ?: return@launch
            val user = authRepository.getUser()
            val senderName = user?.displayName ?: "Unknown"
            val senderRole = user?.role ?: "tester"

            appRepository.sendChat(token, bug._id, message, senderRole, senderName)
                .onSuccess { responseBody ->
                    val response = responseBody as ResponseBody
                    val responseString = response.string()
                    android.util.Log.d("MainViewModel", "Chat Response: $responseString")
                    
                    try {
                        // Manually parse if it is JSON
                        val gson = com.google.gson.Gson()
                        val updatedBug: BugDto = gson.fromJson(responseString, BugDto::class.java)
                        
                        _selectedBug.value = updatedBug
                        _chatInput.value = ""
                        
                        // Also update the bug in the list if present
                        val currentList = _appBugs.value.toMutableList()
                        val index = currentList.indexOfFirst { it._id == updatedBug._id }
                        if (index != -1) {
                            currentList[index] = updatedBug
                            _appBugs.value = currentList
                        }
                    } catch (e: Exception) {
                         android.util.Log.e("MainViewModel", "Failed to parse chat response: $responseString", e)
                         _toastMessage.value = "Sent, but response was: $responseString"
                         loadAppBugs(bug.appId._id)
                    }
                }
                .onFailure { e ->
                    // ... existing failure handling ...
                    val errorMessage = if (e is retrofit2.HttpException) {
                         try {
                             val errorBody = e.response()?.errorBody()?.string()
                             "HTTP ${e.code()}: $errorBody"
                         } catch (ex: Exception) {
                             "HTTP ${e.code()}: ${e.message()}"
                         }
                     } else {
                         e.message ?: "Unknown error"
                     }
                     android.util.Log.e("MainViewModel", "Error sending chat: $errorMessage", e)
                     _toastMessage.value = "Failed: $errorMessage"
                }
        }
    }
}
