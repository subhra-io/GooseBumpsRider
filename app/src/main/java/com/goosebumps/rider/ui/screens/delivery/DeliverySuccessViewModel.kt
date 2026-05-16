package com.goosebumps.rider.ui.screens.delivery

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.goosebumps.rider.domain.repository.OrderRepository
import com.goosebumps.rider.domain.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DeliverySuccessUiState(
    val earnings: Double = 0.0,
    val tipAmount: Double = 0.0,
    val distanceKm: Double = 0.0,
    val timeTakenMinutes: Int = 0,
    val rating: String = "5.0"
)

@HiltViewModel
class DeliverySuccessViewModel @Inject constructor(
    private val orderRepository: OrderRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DeliverySuccessUiState())
    val uiState: StateFlow<DeliverySuccessUiState> = _uiState

    fun loadOrder(orderId: String) {
        viewModelScope.launch {
            when (val result = orderRepository.getOrderDetails(orderId)) {
                is Result.Success -> {
                    val order = result.data
                    _uiState.update {
                        it.copy(
                            earnings = order.estimatedEarnings,
                            tipAmount = order.tipAmount,
                            distanceKm = order.deliveryDistanceKm,
                            timeTakenMinutes = order.estimatedDeliveryMinutes
                        )
                    }
                }
                else -> {}
            }
        }
    }
}
