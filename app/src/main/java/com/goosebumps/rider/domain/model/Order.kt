package com.goosebumps.rider.domain.model

data class Order(
    val id: String,
    val orderNumber: String,
    val status: OrderStatus,
    val restaurant: Restaurant,
    val customer: Customer,
    val items: List<OrderItem>,
    val pickupAddress: Address,
    val deliveryAddress: Address,
    val pickupDistanceKm: Double,
    val deliveryDistanceKm: Double,
    val estimatedEarnings: Double,
    val estimatedDeliveryMinutes: Int,
    val deliveryOtp: String?,
    val specialInstructions: String?,
    val createdAt: String,
    val tipAmount: Double = 0.0,
    val surgeMultiplier: Double = 1.0
)

enum class OrderStatus {
    PLACED, ACCEPTED, PREPARING, READY, PICKED_UP, DELIVERED, CANCELLED;

    companion object {
        fun from(value: String): OrderStatus = entries.find {
            it.name.equals(value, ignoreCase = true)
        } ?: PLACED
    }
}

data class Restaurant(
    val id: String,
    val name: String,
    val phone: String,
    val address: String,
    val lat: Double,
    val lng: Double,
    val image: String?
)

data class Customer(
    val id: String,
    val name: String,
    val phone: String
)

data class OrderItem(
    val id: String,
    val name: String,
    val quantity: Int,
    val price: Double,
    val isVeg: Boolean
)

data class Address(
    val line1: String,
    val line2: String?,
    val city: String,
    val lat: Double,
    val lng: Double
) {
    val fullAddress: String get() = buildString {
        append(line1)
        if (!line2.isNullOrBlank()) append(", $line2")
        append(", $city")
    }
}
