package com.goosebumps.rider.data.remote.dto

import com.google.gson.annotations.SerializedName

data class SendOtpRequest(
    @SerializedName("phone") val phone: String,
    @SerializedName("country_code") val countryCode: String
)

data class VerifyOtpRequest(
    @SerializedName("phone") val phone: String,
    @SerializedName("country_code") val countryCode: String,
    @SerializedName("otp") val otp: String
)

data class AuthResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("token") val token: String?,
    @SerializedName("rider") val rider: RiderDto?,
    @SerializedName("message") val message: String?
)

data class RiderDto(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("phone") val phone: String,
    @SerializedName("profile_image") val profileImage: String?,
    @SerializedName("vehicle_type") val vehicleType: String,
    @SerializedName("vehicle_number") val vehicleNumber: String,
    @SerializedName("rating") val rating: Float,
    @SerializedName("total_deliveries") val totalDeliveries: Int,
    @SerializedName("wallet_balance") val walletBalance: Double,
    @SerializedName("is_online") val isOnline: Boolean,
    @SerializedName("language") val language: String = "en"
)
