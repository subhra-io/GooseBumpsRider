package com.goosebumps.rider.data.remote.dto

import com.google.gson.annotations.SerializedName

data class EarningsDto(
    @SerializedName("today") val today: Double,
    @SerializedName("this_week") val thisWeek: Double,
    @SerializedName("this_month") val thisMonth: Double,
    @SerializedName("orders_today") val ordersToday: Int,
    @SerializedName("orders_week") val ordersWeek: Int,
    @SerializedName("orders_month") val ordersMonth: Int,
    @SerializedName("incentives") val incentives: Double,
    @SerializedName("bonuses") val bonuses: Double,
    @SerializedName("fuel_estimate") val fuelEstimate: Double,
    @SerializedName("performance_score") val performanceScore: Float,
    @SerializedName("weekly_data") val weeklyData: List<DailyEarningDto>,
    @SerializedName("monthly_data") val monthlyData: List<DailyEarningDto>
)

data class DailyEarningDto(
    @SerializedName("date") val date: String,
    @SerializedName("amount") val amount: Double,
    @SerializedName("orders") val orders: Int
)

data class LocationUpdateRequest(
    @SerializedName("lat") val lat: Double,
    @SerializedName("lng") val lng: Double,
    @SerializedName("speed") val speed: Float,
    @SerializedName("battery") val battery: Int,
    @SerializedName("accuracy") val accuracy: Float
)

data class RiderStatusRequest(
    @SerializedName("is_online") val isOnline: Boolean
)

data class ApiResponse<T>(
    @SerializedName("success") val success: Boolean,
    @SerializedName("data") val data: T?,
    @SerializedName("message") val message: String?,
    @SerializedName("error_code") val errorCode: String?
)
