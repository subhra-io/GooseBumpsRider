package com.goosebumps.rider.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cached_orders")
data class CachedOrderEntity(
    @PrimaryKey val id: String,
    val orderNumber: String,
    val status: String,
    val restaurantName: String,
    val restaurantPhone: String,
    val restaurantLat: Double,
    val restaurantLng: Double,
    val customerName: String,
    val customerPhone: String,
    val deliveryLat: Double,
    val deliveryLng: Double,
    val deliveryAddress: String,
    val estimatedEarnings: Double,
    val tipAmount: Double,
    val deliveryOtp: String?,
    val createdAt: String,
    val deliveredAt: String?,
    val itemsJson: String // JSON serialized list
)
