package com.goosebumps.rider.ui.screens.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.goosebumps.rider.domain.usecase.auth.SendOtpUseCase
import com.goosebumps.rider.domain.usecase.auth.VerifyOtpUseCase
import com.goosebumps.rider.domain.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class OtpUiState(
    val otp: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val timerSeconds: Int = 30,
    val canResend: Boolean = false,
    val navigateToHome: Boolean = false
)

@HiltViewModel
class OtpViewModel @Inject constructor(
    private val verifyOtpUseCase: VerifyOtpUseCase,
    private val sendOtpUseCase: SendOtpUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(OtpUiState())
    val uiState: StateFlow<OtpUiState> = _uiState

    private var phone = ""
    private var countryCode = ""
    private var timerJob: Job? = null

    fun init(phone: String, countryCode: String) {
        this.phone = phone
        this.countryCode = countryCode
        startTimer()
    }

    fun onOtpChanged(otp: String) {
        _uiState.update { it.copy(otp = otp, error = null) }
        if (otp.length == 6) verifyOtp()
    }

    fun verifyOtp() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            when (val result = verifyOtpUseCase(phone, countryCode, _uiState.value.otp)) {
                is Result.Success -> _uiState.update { it.copy(isLoading = false, navigateToHome = true) }
                is Result.Error -> _uiState.update { it.copy(isLoading = false, error = result.message, otp = "") }
                else -> _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun resendOtp() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, canResend = false, error = null) }
            sendOtpUseCase(phone, countryCode)
            _uiState.update { it.copy(isLoading = false) }
            startTimer()
        }
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            var seconds = 30
            while (seconds > 0) {
                _uiState.update { it.copy(timerSeconds = seconds, canResend = false) }
                delay(1000)
                seconds--
            }
            _uiState.update { it.copy(canResend = true, timerSeconds = 0) }
        }
    }

    override fun onCleared() {
        timerJob?.cancel()
        super.onCleared()
    }
}
