package com.goosebumps.rider.ui.screens.login

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.goosebumps.rider.ui.components.*
import com.goosebumps.rider.ui.theme.*

@Composable
fun OtpScreen(
    phone: String,
    countryCode: String,
    onNavigateToHome: () -> Unit,
    viewModel: OtpViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(phone, countryCode) {
        viewModel.init(phone, countryCode)
    }

    LaunchedEffect(uiState.navigateToHome) {
        if (uiState.navigateToHome) onNavigateToHome()
    }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(GbBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.padding(top = 60.dp)) {
                Text(text = "Verify OTP", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "Sent to $countryCode $phone",
                    fontSize = 14.sp,
                    color = GbOnSurfaceDim
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
                // Hidden text field to capture input
                androidx.compose.foundation.text.BasicTextField(
                    value = uiState.otp,
                    onValueChange = { if (it.length <= 6 && it.all { c -> c.isDigit() }) viewModel.onOtpChanged(it) },
                    modifier = Modifier
                        .size(1.dp)
                        .focusRequester(focusRequester),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword)
                )

                // Visual OTP boxes
                OtpInputRow(
                    otpValue = uiState.otp,
                    otpLength = 6,
                    onOtpChange = { viewModel.onOtpChanged(it) }
                )

                // Error
                AnimatedVisibility(visible = uiState.error != null) {
                    ErrorMessage(message = uiState.error ?: "")
                }

                // Timer / Resend
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (uiState.canResend) {
                        TextButton(onClick = { viewModel.resendOtp() }) {
                            Text("Resend OTP", color = GbOrange, fontWeight = FontWeight.SemiBold)
                        }
                    } else {
                        Text(
                            text = "Resend in ",
                            fontSize = 14.sp,
                            color = GbOnSurfaceDim
                        )
                        CountdownTimer(secondsRemaining = uiState.timerSeconds)
                    }
                }

                RiderPrimaryButton(
                    text = "Verify & Continue",
                    onClick = { viewModel.verifyOtp() },
                    isLoading = uiState.isLoading,
                    enabled = uiState.otp.length == 6
                )
            }

            Spacer(Modifier.height(32.dp))
        }

        LoadingOverlay(isVisible = uiState.isLoading)
    }
}
