package com.goosebumps.rider.domain.repository

import com.goosebumps.rider.domain.model.Order
import com.goosebumps.rider.domain.util.Result
import kotlinx.coroutines.flow.Flow

interface OrderRepository {
    suspend fun getActiveOrder(): Result<Order?>
    suspend fun getOrderDetails(orderId: String): Result<Order>
    fun getOrderHistory(): Flow<List<Order>>
    suspend fun fetchOrderHistory(page: Int): Result<List<Order>>
    suspend fun acceptOrder(orderId: String): Result<Order>
    suspend fun declineOrder(orderId: String): Result<Unit>
    suspend fun confirmPickup(orderId: String): Result<Order>
    suspend fun verifyDeliveryOtp(orderId: String, otp: String): Result<Order>
    suspend fun reportDelay(orderId: String, reason: String): Result<Unit>
}
