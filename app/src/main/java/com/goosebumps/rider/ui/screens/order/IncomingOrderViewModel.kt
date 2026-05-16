package com.goosebumps.rider.ui.screens.order

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.goosebumps.rider.domain.repository.OrderRepository
import com.goosebumps.rider.domain.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class IncomingOrderUiState(
    val restaurantName: String = "",
    val restaurantLat: Double = 0.0,
    val restaurantLng: Double = 0.0,
    val pickupDistanceKm: Double = 0.0,
    val deliveryDistanceKm: Double = 0.0,
    val deliveryAddress: String = "",
    val estimatedEarnings: Double = 0.0,
    val estimatedMinutes: Int = 0,
    val itemCount: Int = 0,
    val surgeMultiplier: Double = 1.0,
    val timerSeconds: Int = 30,
    val timerProgress: Float = 1f,
    val isLoading: Boolean = false,
    val accepted: Boolean = false,
    val declined: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class IncomingOrderViewModel @Inject constructor(
    private val orderRepository: OrderRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(IncomingOrderUiState())
    val uiState: StateFlow<IncomingOrderUiState> = _uiState

    private var orderId = ""
    private var timerJob: Job? = null

    fun loadOrder(id: String) {
        orderId = id
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            when (val result = orderRepository.getOrderDetails(id)) {
                is Result.Success -> {
                    val order = result.data
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            restaurantName = order.restaurant.name,
                            restaurantLat = order.restaurant.lat,
                            restaurantLng = order.restaurant.lng,
                            pickupDistanceKm = order.pickupDistanceKm,
                            deliveryDistanceKm = order.deliveryDistanceKm,
                            deliveryAddress = order.deliveryAddress.fullAddress,
                            estimatedEarnings = order.estimatedEarnings,
                            estimatedMinutes = order.estimatedDeliveryMinutes,
                            itemCount = order.items.size,
                            surgeMultiplier = order.surgeMultiplier
                        )
                    }
                    startTimer()
                }
                is Result.Error -> _uiState.update { it.copy(isLoading = false, error = result.message) }
                else -> {}
            }
        }
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            var seconds = 30
            while (seconds > 0) {
                _uiState.update { it.copy(timerSeconds = seconds, timerProgress = seconds / 30f) }
                delay(1000)
                seconds--
            }
            // Auto-decline on timeout
            _uiState.update { it.copy(declined = true) }
        }
    }

    fun acceptOrder() {
        timerJob?.cancel()
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            when (orderRepository.acceptOrder(orderId)) {
                is Result.Success -> _uiState.update { it.copy(isLoading = false, accepted = true) }
                is Result.Error -> _uiState.update { it.copy(isLoading = false, declined = true) }
                else -> {}
            }
        }
    }

    fun declineOrder() {
        timerJob?.cancel()
        viewModelScope.launch {
            orderRepository.declineOrder(orderId)
            _uiState.update { it.copy(declined = true) }
        }
    }

    override fun onCleared() {
        timerJob?.cancel()
        super.onCleared()
    }
}
