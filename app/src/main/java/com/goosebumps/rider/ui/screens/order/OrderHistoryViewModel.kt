package com.goosebumps.rider.ui.screens.order

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.goosebumps.rider.domain.model.Order
import com.goosebumps.rider.domain.repository.OrderRepository
import com.goosebumps.rider.domain.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class OrderHistoryUiState(
    val orders: List<Order> = emptyList(),
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val hasMore: Boolean = false,
    val currentPage: Int = 1
)

@HiltViewModel
class OrderHistoryViewModel @Inject constructor(
    private val orderRepository: OrderRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(OrderHistoryUiState())
    val uiState: StateFlow<OrderHistoryUiState> = _uiState

    private var allOrders: List<Order> = emptyList()

    init {
        observeLocalOrders()
        refresh()
    }

    private fun observeLocalOrders() {
        viewModelScope.launch {
            orderRepository.getOrderHistory().collect { orders ->
                allOrders = orders
                applyFilter()
            }
        }
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, currentPage = 1) }
            when (val result = orderRepository.fetchOrderHistory(1)) {
                is Result.Success -> _uiState.update { it.copy(isLoading = false, hasMore = result.data.size == 20) }
                else -> _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun loadMore() {
        val state = _uiState.value
        if (state.isLoading || !state.hasMore) return
        viewModelScope.launch {
            val nextPage = state.currentPage + 1
            _uiState.update { it.copy(isLoading = true) }
            when (val result = orderRepository.fetchOrderHistory(nextPage)) {
                is Result.Success -> _uiState.update {
                    it.copy(isLoading = false, currentPage = nextPage, hasMore = result.data.size == 20)
                }
                else -> _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun onSearch(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        applyFilter()
    }

    private fun applyFilter() {
        val query = _uiState.value.searchQuery.lowercase()
        val filtered = if (query.isEmpty()) allOrders
        else allOrders.filter {
            it.restaurant.name.lowercase().contains(query) ||
                    it.orderNumber.lowercase().contains(query)
        }
        _uiState.update { it.copy(orders = filtered) }
    }
}
