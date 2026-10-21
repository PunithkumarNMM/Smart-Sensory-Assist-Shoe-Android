package com.smartshoe.app.network

import com.smartshoe.app.model.DeleteResponse
import com.smartshoe.app.models.DeviceRegisterRequest
import com.smartshoe.app.model.DeviceRegisterResponse
import com.smartshoe.app.model.ForgotPasswordRequest
import com.smartshoe.app.model.ForgotPasswordResponse
import com.smartshoe.app.model.GuardianAddResponse
import com.smartshoe.app.model.GuardianRequest
import com.smartshoe.app.model.GuardianResponse
import com.smartshoe.app.model.LoginRequest
import com.smartshoe.app.model.LoginResponse
import com.smartshoe.app.model.NotificationResponse
import com.smartshoe.app.model.RegisterRequest
import com.smartshoe.app.model.RegisterResponse
import com.smartshoe.app.model.SOSHistoryResponse
import com.smartshoe.app.model.SOSRequest
import com.smartshoe.app.model.SOSResponse
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*
import com.smartshoe.app.model.ProfileResponse
import com.smartshoe.app.profile.UpdateProfileRequest
import com.smartshoe.app.ai.AiRequest
import com.smartshoe.app.ai.AiResponse
import com.smartshoe.app.model.ChangePasswordRequest
import com.smartshoe.app.model.ChangePasswordResponse
import okhttp3.MultipartBody
import retrofit2.http.Multipart
import retrofit2.http.Part
import com.smartshoe.app.model.UploadResponse
import com.smartshoe.app.dashboard.DashboardResponse
import com.smartshoe.app.gps.LocationResponse
import com.smartshoe.app.device.DeviceResponse
import com.smartshoe.app.model.UpdateDeviceRequest
import retrofit2.http.Body
import retrofit2.http.PUT


interface ApiService {
    data class CameraStatusResponse(
        val online: Boolean,
        val lastUpdate: Long
    )

    // ==========================
    // AUTH APIs
    // ==========================

    @PUT("api/auth/update-device")
    suspend fun updateDevice(
        @Body request: UpdateDeviceRequest
    ): Response<ResponseBody>

    @POST("api/auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<LoginResponse>

    @POST("api/auth/register")
    suspend fun register(
        @Body request: RegisterRequest
    ): Response<RegisterResponse>

    @POST("api/auth/forgot-password")
    suspend fun forgotPassword(
        @Body request: ForgotPasswordRequest
    ): Response<ForgotPasswordResponse>

    // ==========================
    // DEVICE APIs
    // ==========================

    @POST("api/device/register")
    suspend fun registerDevice(
        @Body request: DeviceRegisterRequest
    ): Response<DeviceRegisterResponse>

    // ==========================
    // SOS APIs
    // ==========================

    @POST("api/sos/send")
    suspend fun sendSOS(
        @Body request: SOSRequest
    ): Response<SOSResponse>

    @GET("api/sos/history/{deviceId}")
    suspend fun getSOSHistory(
        @Path("deviceId") deviceId: String
    ): Response<SOSHistoryResponse>

    // ==========================
    // NOTIFICATION APIs
    // ==========================

    @GET("api/notifications")
    suspend fun getNotifications(): Response<NotificationResponse>

    @PATCH("api/notifications/{id}/read")
    suspend fun markNotificationRead(
        @Path("id") id: String
    ): Response<Unit>

    @PATCH("api/notifications/read-all")
    suspend fun markAllNotificationsRead(): Response<Unit>

    @DELETE("api/notifications/{id}")
    suspend fun deleteNotification(
        @Path("id") id: String
    ): Response<Unit>

    // ==========================
    // CAMERA API
    // ==========================

    @Streaming
    @GET("api/camera/latest")
    suspend fun getLatestImage(): Response<ResponseBody>

    // ==========================
    // GUARDIAN APIs
    // ==========================

    @GET("api/guardians")
    suspend fun getGuardians(): Response<GuardianResponse>

    @POST("api/guardians")
    suspend fun addGuardian(
        @Body request: GuardianRequest
    ): Response<GuardianAddResponse>

    @PUT("api/guardians/{id}")
    suspend fun updateGuardian(
        @Path("id") id: String,
        @Body request: GuardianRequest
    ): Response<GuardianAddResponse>

    @DELETE("api/guardians/{id}")
    suspend fun deleteGuardian(
        @Path("id") id: String
    ): Response<DeleteResponse>
    @GET("api/guardians/{id}")
    suspend fun getGuardianById(
        @Path("id") id: String
    ): Response<GuardianAddResponse>
    @GET("api/auth/profile")
    suspend fun getProfile(): Response<ProfileResponse>
    @PUT("api/auth/profile")
    suspend fun updateProfile(
        @Body request: UpdateProfileRequest
    ): Response<ProfileResponse>
    @POST("api/ai/detect")
    suspend fun detectFall(
        @Body request: AiRequest
    ): Response<AiResponse>
    @PUT("api/auth/change-password")
    suspend fun changePassword(
        @Body request: ChangePasswordRequest
    ): Response<ChangePasswordResponse>
    @Multipart
    @POST("api/auth/upload-profile-image")
    suspend fun uploadProfileImage(
        @Part image: MultipartBody.Part
    ): Response<UploadResponse>
    @GET("api/dashboard/{deviceId}")
    suspend fun getDashboard(
        @Path("deviceId") deviceId: String
    ): Response<DashboardResponse>
    @GET("api/location/live/{deviceId}")
    suspend fun getLiveLocation(
        @Path("deviceId") deviceId: String
    ): Response<LocationResponse>
    @GET("api/camera/status")
    suspend fun getCameraStatus(): Response<CameraStatusResponse>
    @GET("api/device/status/{deviceId}")
    suspend fun getDeviceStatus(
        @Path("deviceId") deviceId: String
    ): Response<DeviceResponse>
}
