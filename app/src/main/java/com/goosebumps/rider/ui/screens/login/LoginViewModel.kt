package com.goosebumps.rider.ui.screens.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.goosebumps.rider.domain.usecase.auth.SendOtpUseCase
import com.goosebumps.rider.domain.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LoginUiState(
    val phone: String = "",
    val selectedCountryCode: String = "+91",
    val selectedFlag: String = "🇮🇳",
    val showCountryPicker: Boolean = false,
    val isLoading: Boolean = false,
    val phoneError: String? = null,
    val navigateToOtp: Boolean = false
)

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val sendOtpUseCase: SendOtpUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState

    val countries = listOf(
        "+91" to "🇮🇳",
        "+1" to "🇺🇸",
        "+44" to "🇬🇧",
        "+971" to "🇦🇪",
        "+65" to "🇸🇬",
        "+60" to "🇲🇾",
        "+880" to "🇧🇩",
        "+94" to "🇱🇰"
    )

    fun onPhoneChanged(phone: String) {
        _uiState.update { it.copy(phone = phone, phoneError = null) }
    }

    fun toggleCountryPicker() {
        _uiState.update { it.copy(showCountryPicker = !it.showCountryPicker) }
    }

    fun selectCountry(code: String, flag: String) {
        _uiState.update { it.copy(selectedCountryCode = code, selectedFlag = flag, showCountryPicker = false) }
    }

    fun sendOtp() {
        val state = _uiState.value
        if (state.phone.length < 10) {
            _uiState.update { it.copy(phoneError = "Enter a valid 10-digit number") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, phoneError = null) }
            when (val result = sendOtpUseCase(state.phone, state.selectedCountryCode)) {
                is Result.Success -> _uiState.update { it.copy(isLoading = false, navigateToOtp = true) }
                is Result.Error -> _uiState.update { it.copy(isLoading = false, phoneError = result.message) }
                else -> _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun onNavigated() {
        _uiState.update { it.copy(navigateToOtp = false) }
    }
}
