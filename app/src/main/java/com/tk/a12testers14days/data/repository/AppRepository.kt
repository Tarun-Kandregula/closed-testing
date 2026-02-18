package com.tk.a12testers14days.data.repository

import com.tk.a12testers14days.data.remote.ApiService
import com.tk.a12testers14days.data.remote.CheckInRequest
import com.tk.a12testers14days.data.remote.SubmitAppRequest
import com.tk.a12testers14days.data.remote.CreateBugRequest

class AppRepository(private val api: ApiService) {

    suspend fun getMyApps(token: String) = runCatching { 
        api.getMyApps("Bearer $token") 
    }

    suspend fun submitApp(token: String, request: SubmitAppRequest) = runCatching { 
        api.submitApp("Bearer $token", request) 
    }

    suspend fun updateApp(token: String, appId: String, request: SubmitAppRequest) = runCatching {
        api.updateApp("Bearer $token", appId, request)
    }

    suspend fun startTesting(token: String, appId: String) = runCatching { 
        api.startTesting("Bearer $token", appId) 
    }

    suspend fun getMyTests(token: String) = runCatching { 
        api.getMyTests("Bearer $token") 
    }

    suspend fun getAvailableApps(token: String) = runCatching { 
        api.getAvailableApps("Bearer $token") 
    }

    suspend fun optIn(token: String, appId: String) = runCatching { 
        api.optIn("Bearer $token", appId) 
    }

    suspend fun checkIn(token: String, appId: String, packageName: String) = runCatching { 
        api.checkIn("Bearer $token", CheckInRequest(appId, packageName)) 
    }

    suspend fun reportBug(token: String, appId: String, title: String, description: String) = runCatching {
        api.reportBug("Bearer $token", CreateBugRequest(appId, title, description))
    }

    suspend fun getAppBugs(token: String, appId: String) = runCatching {
        api.getAppBugs("Bearer $token", appId)
    }

    suspend fun getMyBugs(token: String) = runCatching {
        api.getMyBugs("Bearer $token")
    }

    suspend fun sendChat(token: String, bugId: String, message: String, senderRole: String, senderName: String) = runCatching {
        api.sendChat("Bearer $token", bugId, com.tk.a12testers14days.data.remote.ChatRequest(message, senderRole, senderName))
    }
}
