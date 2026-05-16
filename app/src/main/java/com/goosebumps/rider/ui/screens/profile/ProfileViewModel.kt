package com.goosebumps.rider.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.goosebumps.rider.domain.repository.AuthRepository
import com.goosebumps.rider.domain.repository.RiderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileUiState(
    val name: String = "",
    val phone: String = "",
    val profileImage: String? = null,
    val vehicleType: String = "",
    val vehicleNumber: String = "",
    val rating: Float = 0f,
    val totalDeliveries: Int = 0,
    val walletBalance: Double = 0.0
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val riderRepository: RiderRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState

    init {
        viewModelScope.launch {
            riderRepository.getProfile().collect { rider ->
                rider?.let {
                    _uiState.update { state ->
                        state.copy(
                            name = it.name,
                            phone = it.phone,
                            profileImage = it.profileImage,
                            vehicleType = it.vehicleType,
                            vehicleNumber = it.vehicleNumber,
                            rating = it.rating,
                            totalDeliveries = it.totalDeliveries,
                            walletBalance = it.walletBalance
                        )
                    }
                }
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
        }
    }

    fun triggerSOS() {
        // In production: send SOS alert to emergency contacts + backend
    }
}
