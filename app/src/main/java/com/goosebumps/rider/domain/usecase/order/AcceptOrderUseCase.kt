package com.goosebumps.rider.domain.usecase.order

import com.goosebumps.rider.domain.model.Order
import com.goosebumps.rider.domain.repository.OrderRepository
import com.goosebumps.rider.domain.util.Result
import javax.inject.Inject

class AcceptOrderUseCase @Inject constructor(
    private val orderRepository: OrderRepository
) {
    suspend operator fun invoke(orderId: String): Result<Order> =
        orderRepository.acceptOrder(orderId)
}
