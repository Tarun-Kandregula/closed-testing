package com.tk.a12testers14days.data.remote

import com.google.gson.annotations.SerializedName
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {
    @POST("auth/signup")
    suspend fun signup(@Body request: SignupRequest): LoginResponse

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    // Developer Endpoints
    @GET("apps/my-apps")
    suspend fun getMyApps(@Header("Authorization") token: String): List<AppDto>

    @POST("apps/submit")
    suspend fun submitApp(@Header("Authorization") token: String, @Body request: SubmitAppRequest): SubmitAppResponse

    @PUT("apps/{id}")
    suspend fun updateApp(@Header("Authorization") token: String, @Path("id") appId: String, @Body request: SubmitAppRequest): SubmitAppResponse

    @PUT("apps/{id}/start")
    suspend fun startTesting(@Header("Authorization") token: String, @Path("id") appId: String): GenericResponse

    // Tester Endpoints
    @GET("apps/my-tests")
    suspend fun getMyTests(@Header("Authorization") token: String): List<AppDto>

    @GET("apps/available")
    suspend fun getAvailableApps(@Header("Authorization") token: String): List<AppDto>

    @POST("apps/{id}/opt-in")
    suspend fun optIn(@Header("Authorization") token: String, @Path("id") appId: String): GenericResponse

    @PUT("apps/check-in")
    suspend fun checkIn(@Header("Authorization") token: String, @Body request: CheckInRequest): CheckInResponse

    @POST("bugs")
    suspend fun reportBug(@Header("Authorization") token: String, @Body request: CreateBugRequest): BugDto

    @GET("bugs/app/{appId}")
    suspend fun getAppBugs(@Header("Authorization") token: String, @Path("appId") appId: String): List<BugDto>

    @GET("bugs/my-reports")
    suspend fun getMyBugs(@Header("Authorization") token: String): List<BugDto>

    @POST("bugs/{id}/chat")
    suspend fun sendChat(@Header("Authorization") token: String, @Path("id") bugId: String, @Body request: ChatRequest): okhttp3.ResponseBody
}

// Auth Data Classes
data class SignupRequest(val email: String, val password: String, val role: String, val displayName: String)
data class LoginRequest(val email: String, val password: String, val role: String)
data class LoginResponse(val token: String, val user: UserDto)
data class UserDto(val _id: String, val displayName: String, val email: String, val role: String)

// App Data Classes
data class AppDto(
    @SerializedName("_id") val id: String,
    val appName: String,
    val appVersion: String?,
    val appDescription: String?,
    val packageName: String?,
    val closedTestingLink: String?,
    val appIcon: String?,
    val status: String,
    val maxTesters: Int?,
    val testersRequired: Int?,
    val durationDays: Int?,
    val paymentAmount: Int?,
    val optedInTesters: List<OptedInTesterDto>?,
    val currentTesters: List<TesterDetailDto>?,
    val developerId: DeveloperDto?
)

data class OptedInTesterDto(
    val testerId: UserDto?, 
    val daysCompleted: Int,
    val lastCheckIn: String?,
    val status: String?
)

data class TesterDetailDto(
    val _id: String,
    val displayName: String,
    val email: String,
    val deviceModel: String?,
    val androidVersion: String?
)

data class DeveloperDto(val _id: String, val displayName: String, val email: String)

// Request/Response Classes
data class SubmitAppRequest(
    val appName: String,
    val packageName: String,
    val appVersion: String,
    val closedTestingLink: String,
    val appDescription: String,
    val paymentAmount: Int,
    val maxTesters: Int,
    val durationDays: Int
)
data class SubmitAppResponse(val success: Boolean, val message: String, val app: AppDto)
data class CheckInRequest(val appId: String, val installedPackageName: String)
data class CheckInResponse(val success: Boolean, val message: String, val daysCompleted: Int)
data class GenericResponse(val success: Boolean, val message: String)

