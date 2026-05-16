package com.goosebumps.rider.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "rider_profile")
data class RiderProfileEntity(
    @PrimaryKey val id: String,
    val name: String,
    val phone: String,
    val profileImage: String?,
    val vehicleType: String,
    val vehicleNumber: String,
    val rating: Float,
    val totalDeliveries: Int,
    val walletBalance: Double,
    val isOnline: Boolean,
    val language: String
)
