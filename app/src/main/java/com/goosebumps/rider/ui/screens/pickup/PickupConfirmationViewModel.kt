package com.goosebumps.rider.ui.screens.pickup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.goosebumps.rider.domain.model.OrderItem
import com.goosebumps.rider.domain.repository.OrderRepository
import com.goosebumps.rider.domain.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PickupConfirmUiState(
    val restaurantName: String = "",
    val orderNumber: String = "",
    val items: List<OrderItem> = emptyList(),
    val waitingSeconds: Int = 0,
    val isLoading: Boolean = false,
    val pickedUp: Boolean = false
)

@HiltViewModel
class PickupConfirmationViewModel @Inject constructor(
    private val orderRepository: OrderRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PickupConfirmUiState())
    val uiState: StateFlow<PickupConfirmUiState> = _uiState

    private var orderId = ""
    private var timerJob: Job? = null

    fun loadOrder(id: String) {
        orderId = id
        viewModelScope.launch {
            when (val result = orderRepository.getOrderDetails(id)) {
                is Result.Success -> {
                    val order = result.data
                    _uiState.update {
                        it.copy(
                            restaurantName = order.restaurant.name,
                            orderNumber = order.orderNumber,
                            items = order.items
                        )
                    }
                    startWaitingTimer()
                }
                else -> {}
            }
        }
    }

    private fun startWaitingTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (true) {
                delay(1000)
                _uiState.update { it.copy(waitingSeconds = it.waitingSeconds + 1) }
            }
        }
    }

    fun confirmPickup() {
        timerJob?.cancel()
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            when (orderRepository.confirmPickup(orderId)) {
                is Result.Success -> _uiState.update { it.copy(isLoading = false, pickedUp = true) }
                is Result.Error -> _uiState.update { it.copy(isLoading = false) }
                else -> {}
            }
        }
    }

    fun reportDelay() {
        viewModelScope.launch {
            orderRepository.reportDelay(orderId, "Restaurant not ready")
        }
    }

    override fun onCleared() {
        timerJob?.cancel()
        super.onCleared()
    }
}
