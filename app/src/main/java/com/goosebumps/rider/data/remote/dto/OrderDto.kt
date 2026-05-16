package com.goosebumps.rider.data.remote.dto

import com.google.gson.annotations.SerializedName

data class OrderDto(
    @SerializedName("id") val id: String,
    @SerializedName("order_number") val orderNumber: String,
    @SerializedName("status") val status: String,
    @SerializedName("restaurant") val restaurant: RestaurantDto,
    @SerializedName("customer") val customer: CustomerDto,
    @SerializedName("items") val items: List<OrderItemDto>,
    @SerializedName("pickup_address") val pickupAddress: AddressDto,
    @SerializedName("delivery_address") val deliveryAddress: AddressDto,
    @SerializedName("pickup_distance_km") val pickupDistanceKm: Double,
    @SerializedName("delivery_distance_km") val deliveryDistanceKm: Double,
    @SerializedName("estimated_earnings") val estimatedEarnings: Double,
    @SerializedName("estimated_delivery_minutes") val estimatedDeliveryMinutes: Int,
    @SerializedName("delivery_otp") val deliveryOtp: String?,
    @SerializedName("special_instructions") val specialInstructions: String?,
    @SerializedName("created_at") val createdAt: String,
    @SerializedName("accepted_at") val acceptedAt: String?,
    @SerializedName("picked_up_at") val pickedUpAt: String?,
    @SerializedName("delivered_at") val deliveredAt: String?,
    @SerializedName("tip_amount") val tipAmount: Double = 0.0,
    @SerializedName("surge_multiplier") val surgeMultiplier: Double = 1.0
)

data class RestaurantDto(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("phone") val phone: String,
    @SerializedName("address") val address: String,
    @SerializedName("lat") val lat: Double,
    @SerializedName("lng") val lng: Double,
    @SerializedName("image") val image: String?
)

data class CustomerDto(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("phone") val phone: String
)

data class OrderItemDto(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("quantity") val quantity: Int,
    @SerializedName("price") val price: Double,
    @SerializedName("is_veg") val isVeg: Boolean
)

data class AddressDto(
    @SerializedName("line1") val line1: String,
    @SerializedName("line2") val line2: String?,
    @SerializedName("city") val city: String,
    @SerializedName("lat") val lat: Double,
    @SerializedName("lng") val lng: Double
)

data class OrderListResponse(
    @SerializedName("orders") val orders: List<OrderDto>,
    @SerializedName("total") val total: Int,
    @SerializedName("page") val page: Int,
    @SerializedName("has_more") val hasMore: Boolean
)

data class AcceptOrderRequest(
    @SerializedName("order_id") val orderId: String
)

data class VerifyDeliveryOtpRequest(
    @SerializedName("order_id") val orderId: String,
    @SerializedName("otp") val otp: String
)

data class OrderActionResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String?,
    @SerializedName("order") val order: OrderDto?
)
