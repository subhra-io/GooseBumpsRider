package com.goosebumps.rider.ui.screens.delivery

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.goosebumps.rider.data.repository.RoutingRepository
import com.goosebumps.rider.domain.repository.OrderRepository
import com.goosebumps.rider.domain.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject

data class DeliveryNavUiState(
    val customerName: String = "",
    val customerPhone: String = "",
    val deliveryAddress: String = "",
    val deliveryLat: Double = 0.0,
    val deliveryLng: Double = 0.0,
    val routePoints: List<Pair<Double, Double>> = emptyList(),
    val distanceKm: Double = 0.0,
    val etaMinutes: Int = 0,
    val specialInstructions: String = "",
    val showSafetyAlert: Boolean = false,
    val isLoading: Boolean = false,
    val delivered: Boolean = false
)

@HiltViewModel
class DeliveryNavigationViewModel @Inject constructor(
    private val orderRepository: OrderRepository,
    private val routingRepository: RoutingRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(DeliveryNavUiState())
    val uiState: StateFlow<DeliveryNavUiState> = _uiState

    private var orderId = ""

    fun loadOrder(id: String) {
        orderId = id
        viewModelScope.launch {
            when (val result = orderRepository.getOrderDetails(id)) {
                is Result.Success -> {
                    val order = result.data
                    _uiState.update {
                        it.copy(
                            customerName = order.customer.name,
                            customerPhone = order.customer.phone,
                            deliveryAddress = order.deliveryAddress.fullAddress,
                            deliveryLat = order.deliveryAddress.lat,
                            deliveryLng = order.deliveryAddress.lng,
                            etaMinutes = order.estimatedDeliveryMinutes,
                            specialInstructions = order.specialInstructions ?: ""
                        )
                    }
                    fetchRoute(order.deliveryAddress.lat, order.deliveryAddress.lng)
                }
                else -> {}
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun fetchRoute(destLat: Double, destLng: Double) {
        viewModelScope.launch {
            try {
                val location = com.google.android.gms.location.LocationServices
                    .getFusedLocationProviderClient(context)
                    .lastLocation
                    .await()
                val fromLat = location?.latitude ?: destLat
                val fromLng = location?.longitude ?: destLng
                when (val route = routingRepository.getRoute(fromLat, fromLng, destLat, destLng)) {
                    is Result.Success -> {
                        _uiState.update {
                            it.copy(
                                routePoints = route.data.points,
                                distanceKm = route.data.distanceMeters / 1000.0,
                                etaMinutes = (route.data.durationSeconds / 60).toInt()
                            )
                        }
                    }
                    else -> {}
                }
            } catch (e: Exception) {
                Timber.e(e, "Delivery route fetch failed")
            }
        }
    }

    fun callCustomer() {
        val phone = _uiState.value.customerPhone
        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone")).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    }

    fun openNavigation() {
        val state = _uiState.value
        val uri = Uri.parse("geo:${state.deliveryLat},${state.deliveryLng}?q=${state.deliveryLat},${state.deliveryLng}")
        val intent = Intent(Intent.ACTION_VIEW, uri).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            val webUri = Uri.parse("https://www.geoapify.com/maps?lat=${state.deliveryLat}&lon=${state.deliveryLng}")
            context.startActivity(Intent(Intent.ACTION_VIEW, webUri).apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK })
        }
    }

    fun markDelivered() {
        _uiState.update { it.copy(delivered = true) }
    }
}
