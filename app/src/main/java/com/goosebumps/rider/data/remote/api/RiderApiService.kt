package com.goosebumps.rider.data.remote.api

import com.goosebumps.rider.data.remote.dto.*
import retrofit2.Response
import retrofit2.http.*

interface RiderApiService {

    // Auth
    @POST("auth/rider/send-otp")
    suspend fun sendOtp(@Body request: SendOtpRequest): Response<ApiResponse<Unit>>

    @POST("auth/rider/verify-otp")
    suspend fun verifyOtp(@Body request: VerifyOtpRequest): Response<AuthResponse>

    @POST("auth/rider/logout")
    suspend fun logout(): Response<ApiResponse<Unit>>

    // Rider
    @GET("rider/profile")
    suspend fun getProfile(): Response<ApiResponse<RiderDto>>

    @PUT("rider/status")
    suspend fun updateStatus(@Body request: RiderStatusRequest): Response<ApiResponse<RiderDto>>

    @POST("rider/location")
    suspend fun updateLocation(@Body request: LocationUpdateRequest): Response<ApiResponse<Unit>>

    @GET("rider/earnings")
    suspend fun getEarnings(): Response<ApiResponse<EarningsDto>>

    // Orders
    @GET("rider/orders/active")
    suspend fun getActiveOrder(): Response<ApiResponse<OrderDto>>

    @GET("rider/orders/history")
    suspend fun getOrderHistory(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): Response<ApiResponse<OrderListResponse>>

    @GET("rider/orders/{orderId}")
    suspend fun getOrderDetails(@Path("orderId") orderId: String): Response<ApiResponse<OrderDto>>

    @POST("rider/orders/accept")
    suspend fun acceptOrder(@Body request: AcceptOrderRequest): Response<OrderActionResponse>

    @POST("rider/orders/{orderId}/decline")
    suspend fun declineOrder(@Path("orderId") orderId: String): Response<OrderActionResponse>

    @POST("rider/orders/{orderId}/pickup-confirm")
    suspend fun confirmPickup(@Path("orderId") orderId: String): Response<OrderActionResponse>

    @POST("rider/orders/verify-delivery-otp")
    suspend fun verifyDeliveryOtp(@Body request: VerifyDeliveryOtpRequest): Response<OrderActionResponse>

    @POST("rider/orders/{orderId}/report-delay")
    suspend fun reportDelay(
        @Path("orderId") orderId: String,
        @Query("reason") reason: String
    ): Response<ApiResponse<Unit>>

    // FCM Token
    @PUT("rider/fcm-token")
    suspend fun updateFcmToken(@Body body: Map<String, String>): Response<ApiResponse<Unit>>
}
