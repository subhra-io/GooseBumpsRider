package com.goosebumps.rider.ui.navigation

object NavRoutes {
    const val SPLASH = "splash"
    const val LOGIN = "login"
    const val OTP = "otp/{phone}/{countryCode}"
    const val HOME = "home"
    const val INCOMING_ORDER = "incoming_order/{orderId}"
    const val PICKUP_NAVIGATION = "pickup_navigation/{orderId}"
    const val PICKUP_CONFIRMATION = "pickup_confirmation/{orderId}"
    const val DELIVERY_NAVIGATION = "delivery_navigation/{orderId}"
    const val DELIVERY_OTP = "delivery_otp/{orderId}"
    const val DELIVERY_SUCCESS = "delivery_success/{orderId}"
    const val EARNINGS = "earnings"
    const val ORDER_HISTORY = "order_history"
    const val PROFILE = "profile"
    const val SETTINGS = "settings"

    fun otpRoute(phone: String, countryCode: String) = "otp/$phone/$countryCode"
    fun incomingOrderRoute(orderId: String) = "incoming_order/$orderId"
    fun pickupNavRoute(orderId: String) = "pickup_navigation/$orderId"
    fun pickupConfirmRoute(orderId: String) = "pickup_confirmation/$orderId"
    fun deliveryNavRoute(orderId: String) = "delivery_navigation/$orderId"
    fun deliveryOtpRoute(orderId: String) = "delivery_otp/$orderId"
    fun deliverySuccessRoute(orderId: String) = "delivery_success/$orderId"
}
