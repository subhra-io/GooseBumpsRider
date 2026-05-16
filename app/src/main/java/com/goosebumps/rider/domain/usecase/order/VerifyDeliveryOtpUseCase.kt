package com.goosebumps.rider.domain.usecase.order

import com.goosebumps.rider.domain.model.Order
import com.goosebumps.rider.domain.repository.OrderRepository
import com.goosebumps.rider.domain.util.Result
import javax.inject.Inject

class VerifyDeliveryOtpUseCase @Inject constructor(
    private val orderRepository: OrderRepository
) {
    suspend operator fun invoke(orderId: String, otp: String): Result<Order> {
        if (otp.length != 4 && otp.length != 6) return Result.Error("Invalid OTP")
        return orderRepository.verifyDeliveryOtp(orderId, otp)
    }
}
