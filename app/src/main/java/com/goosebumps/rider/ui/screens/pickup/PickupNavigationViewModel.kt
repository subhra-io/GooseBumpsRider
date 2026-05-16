package com.goosebumps.rider.ui.screens.pickup

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
import javax.inject.Inject

data class PickupNavUiState(
    val restaurantName: String = "",
    val restaurantAddress: String = "",
    val restaurantPhone: String = "",
    val restaurantLat: Double = 0.0,
    val restaurantLng: Double = 0.0,
    val routePoints: List<Pair<Double, Double>> = emptyList(),
    val distanceKm: Double = 0.0,
    val etaMinutes: Int = 0,
    val isLoading: Boolean = false,
    val arrived: Boolean = false
)

@HiltViewModel
class PickupNavigationViewModel @Inject constructor(
    private val orderRepository: OrderRepository,
    private val routingRepository: RoutingRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(PickupNavUiState())
    val uiState: StateFlow<PickupNavUiState> = _uiState

    private var orderId = ""

    fun loadOrder(id: String) {
        orderId = id
        viewModelScope.launch {
            when (val result = orderRepository.getOrderDetails(id)) {
                is Result.Success -> {
                    val order = result.data
                    _uiState.update {
                        it.copy(
                            restaurantName = order.restaurant.name,
                            restaurantAddress = order.restaurant.address,
                            restaurantPhone = order.restaurant.phone,
                            restaurantLat = order.restaurant.lat,
                            restaurantLng = order.restaurant.lng,
                            etaMinutes = order.estimatedDeliveryMinutes / 2
                        )
                    }
                    // Fetch real route from Geoapify using fused location as origin
                    fetchRoute(order.restaurant.lat, order.restaurant.lng)
                }
                else -> {}
            }
        }
    }

    private fun fetchRoute(destLat: Double, destLng: Double) {
        viewModelScope.launch {
            // Use last known location as origin; fall back to destination if unavailable
            try {
                @SuppressLint("MissingPermission")
                val location = com.google.android.gms.location.LocationServices
                    .getFusedLocationProviderClient(context)
                    .lastLocation
                    .await()
                val fromLat = location?.latitude ?: destLat
                val fromLng = location?.longitude ?: destLng
                when (val route = routingRepository.getRoute(fromLat, fromLng, destLat, destLng)) {
                    is Result.Success -> {
                        val km = route.data.distanceMeters / 1000.0
                        val mins = (route.data.durationSeconds / 60).toInt()
                        _uiState.update {
                            it.copy(
                                routePoints = route.data.points,
                                distanceKm = km,
                                etaMinutes = mins
                            )
                        }
                    }
                    else -> {}
                }
            } catch (e: Exception) {
                timber.log.Timber.e(e, "Route fetch failed")
            }
        }
    }

    fun callRestaurant() {
        val phone = _uiState.value.restaurantPhone
        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone")).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    }

    fun openNavigation() {
        val state = _uiState.value
        val uri = Uri.parse("google.navigation:q=${state.restaurantLat},${state.restaurantLng}&mode=d")
        val intent = Intent(Intent.ACTION_VIEW, uri).apply {
            setPackage("com.google.android.apps.maps")
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            val webUri = Uri.parse("https://maps.google.com/maps?daddr=${state.restaurantLat},${state.restaurantLng}")
            context.startActivity(Intent(Intent.ACTION_VIEW, webUri).apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK })
        }
    }

    fun markArrived() {
        _uiState.update { it.copy(arrived = true) }
    }
}
