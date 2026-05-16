package com.goosebumps.rider.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.goosebumps.rider.data.local.prefs.SessionManager
import com.goosebumps.rider.data.socket.SocketEvent
import com.goosebumps.rider.data.socket.SocketManager
import com.goosebumps.rider.domain.repository.RiderRepository
import com.goosebumps.rider.domain.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val riderName: String = "",
    val isOnline: Boolean = false,
    val todayEarnings: Double = 0.0,
    val ordersToday: Int = 0,
    val performanceScore: Float = 4.5f,
    val surgeMultiplier: Double = 1.0,
    val hasActiveOrder: Boolean = false,
    val activeOrderNumber: String = "",
    val activeOrderStatus: String = "",
    val incomingOrderId: String? = null,
    val showFatigueAlert: Boolean = false,
    val isLoading: Boolean = false
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val riderRepository: RiderRepository,
    private val socketManager: SocketManager,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState

    init {
        loadProfile()
        loadEarnings()
        observeSocket()
        observeOnlineStatus()
        socketManager.connect()
    }

    private fun loadProfile() {
        viewModelScope.launch {
            riderRepository.getProfile().collect { rider ->
                rider?.let {
                    _uiState.update { state ->
                        state.copy(riderName = it.name, isOnline = it.isOnline)
                    }
                }
            }
        }
        viewModelScope.launch {
            riderRepository.fetchProfile()
        }
    }

    private fun loadEarnings() {
        viewModelScope.launch {
            when (val result = riderRepository.getEarnings()) {
                is Result.Success -> _uiState.update {
                    it.copy(
                        todayEarnings = result.data.today,
                        ordersToday = result.data.ordersToday,
                        performanceScore = result.data.performanceScore
                    )
                }
                else -> {}
            }
        }
    }

    private fun observeSocket() {
        viewModelScope.launch {
            socketManager.events.collect { event ->
                when (event) {
                    is SocketEvent.NewOrder -> {
                        _uiState.update { it.copy(incomingOrderId = event.orderId) }
                    }
                    is SocketEvent.OrderCancelled -> {
                        _uiState.update { it.copy(hasActiveOrder = false) }
                    }
                    else -> {}
                }
            }
        }
    }

    private fun observeOnlineStatus() {
        viewModelScope.launch {
            sessionManager.isOnlineFlow.collect { isOnline ->
                _uiState.update { it.copy(isOnline = isOnline) }
            }
        }
    }

    fun toggleOnlineStatus() {
        viewModelScope.launch {
            val newStatus = !_uiState.value.isOnline
            riderRepository.updateOnlineStatus(newStatus)
            socketManager.emitOnlineStatus(newStatus)
        }
    }

    fun onOrderNavigated() {
        _uiState.update { it.copy(incomingOrderId = null) }
    }

    fun dismissFatigueAlert() {
        _uiState.update { it.copy(showFatigueAlert = false) }
    }

    override fun onCleared() {
        super.onCleared()
    }
}
