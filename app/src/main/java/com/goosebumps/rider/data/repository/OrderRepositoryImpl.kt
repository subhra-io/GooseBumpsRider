package com.goosebumps.rider.data.repository

import com.goosebumps.rider.data.local.db.OrderDao
import com.goosebumps.rider.data.local.entity.CachedOrderEntity
import com.goosebumps.rider.data.remote.api.RiderApiService
import com.goosebumps.rider.data.remote.dto.AcceptOrderRequest
import com.goosebumps.rider.data.remote.dto.OrderDto
import com.goosebumps.rider.data.remote.dto.VerifyDeliveryOtpRequest
import com.goosebumps.rider.domain.model.*
import com.goosebumps.rider.domain.repository.OrderRepository
import com.goosebumps.rider.domain.util.Result
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class OrderRepositoryImpl @Inject constructor(
    private val api: RiderApiService,
    private val orderDao: OrderDao,
    private val gson: Gson
) : OrderRepository {

    override suspend fun getActiveOrder(): Result<Order?> {
        return try {
            val response = api.getActiveOrder()
            if (response.isSuccessful) {
                val order = response.body()?.data?.toDomain()
                Result.Success(order)
            } else Result.Success(null)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Network error")
        }
    }

    override suspend fun getOrderDetails(orderId: String): Result<Order> {
        return try {
            val response = api.getOrderDetails(orderId)
            if (response.isSuccessful) {
                val order = response.body()?.data?.toDomain()
                    ?: return Result.Error("Order not found")
                Result.Success(order)
            } else Result.Error("Failed to load order")
        } catch (e: Exception) {
            // Fallback to cache
            val cached = orderDao.getOrderById(orderId)
            if (cached != null) Result.Success(cached.toDomain(gson))
            else Result.Error(e.message ?: "Network error")
        }
    }

    override fun getOrderHistory(): Flow<List<Order>> {
        return orderDao.getAllOrders().map { list -> list.map { it.toDomain(gson) } }
    }

    override suspend fun fetchOrderHistory(page: Int): Result<List<Order>> {
        return try {
            val response = api.getOrderHistory(page)
            if (response.isSuccessful) {
                val orders = response.body()?.data?.orders?.map { it.toDomain() } ?: emptyList()
                orderDao.insertOrders(orders.map { it.toEntity(gson) })
                Result.Success(orders)
            } else Result.Error("Failed to load history")
        } catch (e: Exception) {
            Result.Error(e.message ?: "Network error")
        }
    }

    override suspend fun acceptOrder(orderId: String): Result<Order> {
        return try {
            val response = api.acceptOrder(AcceptOrderRequest(orderId))
            if (response.isSuccessful) {
                val order = response.body()?.order?.toDomain()
                    ?: return Result.Error("Failed to accept order")
                Result.Success(order)
            } else Result.Error("Failed to accept order")
        } catch (e: Exception) {
            Result.Error(e.message ?: "Network error")
        }
    }

    override suspend fun declineOrder(orderId: String): Result<Unit> {
        return try {
            val response = api.declineOrder(orderId)
            if (response.isSuccessful) Result.Success(Unit)
            else Result.Error("Failed to decline order")
        } catch (e: Exception) {
            Result.Error(e.message ?: "Network error")
        }
    }

    override suspend fun confirmPickup(orderId: String): Result<Order> {
        return try {
            val response = api.confirmPickup(orderId)
            if (response.isSuccessful) {
                val order = response.body()?.order?.toDomain()
                    ?: return Result.Error("Failed to confirm pickup")
                Result.Success(order)
            } else Result.Error("Failed to confirm pickup")
        } catch (e: Exception) {
            Result.Error(e.message ?: "Network error")
        }
    }

    override suspend fun verifyDeliveryOtp(orderId: String, otp: String): Result<Order> {
        return try {
            val response = api.verifyDeliveryOtp(VerifyDeliveryOtpRequest(orderId, otp))
            if (response.isSuccessful) {
                val order = response.body()?.order?.toDomain()
                    ?: return Result.Error("Verification failed")
                Result.Success(order)
            } else Result.Error("Invalid OTP. Please try again.")
        } catch (e: Exception) {
            Result.Error(e.message ?: "Network error")
        }
    }

    override suspend fun reportDelay(orderId: String, reason: String): Result<Unit> {
        return try {
            val response = api.reportDelay(orderId, reason)
            if (response.isSuccessful) Result.Success(Unit)
            else Result.Error("Failed to report delay")
        } catch (e: Exception) {
            Result.Error(e.message ?: "Network error")
        }
    }

    // Mappers
    private fun OrderDto.toDomain() = Order(
        id = id,
        orderNumber = orderNumber,
        status = OrderStatus.from(status),
        restaurant = Restaurant(
            id = restaurant.id,
            name = restaurant.name,
            phone = restaurant.phone,
            address = restaurant.address,
            lat = restaurant.lat,
            lng = restaurant.lng,
            image = restaurant.image
        ),
        customer = Customer(id = customer.id, name = customer.name, phone = customer.phone),
        items = items.map { OrderItem(it.id, it.name, it.quantity, it.price, it.isVeg) },
        pickupAddress = Address(pickupAddress.line1, pickupAddress.line2, pickupAddress.city, pickupAddress.lat, pickupAddress.lng),
        deliveryAddress = Address(deliveryAddress.line1, deliveryAddress.line2, deliveryAddress.city, deliveryAddress.lat, deliveryAddress.lng),
        pickupDistanceKm = pickupDistanceKm,
        deliveryDistanceKm = deliveryDistanceKm,
        estimatedEarnings = estimatedEarnings,
        estimatedDeliveryMinutes = estimatedDeliveryMinutes,
        deliveryOtp = deliveryOtp,
        specialInstructions = specialInstructions,
        createdAt = createdAt,
        tipAmount = tipAmount,
        surgeMultiplier = surgeMultiplier
    )

    private fun Order.toEntity(gson: Gson) = CachedOrderEntity(
        id = id,
        orderNumber = orderNumber,
        status = status.name,
        restaurantName = restaurant.name,
        restaurantPhone = restaurant.phone,
        restaurantLat = restaurant.lat,
        restaurantLng = restaurant.lng,
        customerName = customer.name,
        customerPhone = customer.phone,
        deliveryLat = deliveryAddress.lat,
        deliveryLng = deliveryAddress.lng,
        deliveryAddress = deliveryAddress.fullAddress,
        estimatedEarnings = estimatedEarnings,
        tipAmount = tipAmount,
        deliveryOtp = deliveryOtp,
        createdAt = createdAt,
        deliveredAt = null,
        itemsJson = gson.toJson(items)
    )

    private fun CachedOrderEntity.toDomain(gson: Gson): Order {
        val items = try {
            gson.fromJson(itemsJson, Array<OrderItem>::class.java).toList()
        } catch (e: Exception) { emptyList() }
        return Order(
            id = id,
            orderNumber = orderNumber,
            status = OrderStatus.from(status),
            restaurant = Restaurant(id = "", name = restaurantName, phone = restaurantPhone, address = "", lat = restaurantLat, lng = restaurantLng, image = null),
            customer = Customer(id = "", name = customerName, phone = customerPhone),
            items = items,
            pickupAddress = Address(line1 = "", line2 = null, city = "", lat = restaurantLat, lng = restaurantLng),
            deliveryAddress = Address(line1 = deliveryAddress, line2 = null, city = "", lat = deliveryLat, lng = deliveryLng),
            pickupDistanceKm = 0.0,
            deliveryDistanceKm = 0.0,
            estimatedEarnings = estimatedEarnings,
            estimatedDeliveryMinutes = 0,
            deliveryOtp = deliveryOtp,
            specialInstructions = null,
            createdAt = createdAt,
            tipAmount = tipAmount
        )
    }
}
