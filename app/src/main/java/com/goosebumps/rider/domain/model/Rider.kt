package com.goosebumps.rider.domain.model

data class Rider(
    val id: String,
    val name: String,
    val phone: String,
    val profileImage: String?,
    val vehicleType: String,
    val vehicleNumber: String,
    val rating: Float,
    val totalDeliveries: Int,
    val walletBalance: Double,
    val isOnline: Boolean,
    val language: String = "en"
)

data class Earnings(
    val today: Double,
    val thisWeek: Double,
    val thisMonth: Double,
    val ordersToday: Int,
    val ordersWeek: Int,
    val ordersMonth: Int,
    val incentives: Double,
    val bonuses: Double,
    val fuelEstimate: Double,
    val performanceScore: Float,
    val weeklyData: List<DailyEarning>,
    val monthlyData: List<DailyEarning>
)

data class DailyEarning(
    val date: String,
    val amount: Double,
    val orders: Int
)

data class RiderLocation(
    val lat: Double,
    val lng: Double,
    val speed: Float = 0f,
    val battery: Int = 100,
    val accuracy: Float = 0f
)
