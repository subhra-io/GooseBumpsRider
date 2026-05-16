package com.goosebumps.rider.ui.screens.delivery

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.goosebumps.rider.domain.usecase.order.VerifyDeliveryOtpUseCase
import com.goosebumps.rider.domain.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DeliveryOtpUiState(
    val otp: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val verified: Boolean = false
)

@HiltViewModel
class DeliveryOtpViewModel @Inject constructor(
    private val verifyDeliveryOtpUseCase: VerifyDeliveryOtpUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(DeliveryOtpUiState())
    val uiState: StateFlow<DeliveryOtpUiState> = _uiState

    private var orderId = ""

    fun init(id: String) { orderId = id }

    fun onOtpChanged(otp: String) {
        _uiState.update { it.copy(otp = otp, error = null) }
        if (otp.length == 4) verifyOtp()
    }

    fun verifyOtp() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            when (val result = verifyDeliveryOtpUseCase(orderId, _uiState.value.otp)) {
                is Result.Success -> _uiState.update { it.copy(isLoading = false, verified = true) }
                is Result.Error -> _uiState.update { it.copy(isLoading = false, error = result.message, otp = "") }
                else -> _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun skipOtp() {
        // Allow skip with photo proof in production
        _uiState.update { it.copy(verified = true) }
    }
}
