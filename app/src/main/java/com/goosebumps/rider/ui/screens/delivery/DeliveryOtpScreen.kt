package com.goosebumps.rider.ui.screens.delivery

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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.goosebumps.rider.ui.components.*
import com.goosebumps.rider.ui.theme.*

@Composable
fun DeliveryOtpScreen(
    orderId: String,
    onVerified: () -> Unit,
    viewModel: DeliveryOtpViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(orderId) { viewModel.init(orderId) }
    LaunchedEffect(uiState.verified) { if (uiState.verified) onVerified() }
    LaunchedEffect(Unit) { focusRequester.requestFocus() }

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
                Text("Delivery OTP", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = Color.White)
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "Ask the customer for their OTP to confirm delivery",
                    fontSize = 14.sp,
                    color = GbOnSurfaceDim
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
                // Hidden input
                androidx.compose.foundation.text.BasicTextField(
                    value = uiState.otp,
                    onValueChange = { if (it.length <= 4 && it.all { c -> c.isDigit() }) viewModel.onOtpChanged(it) },
                    modifier = Modifier.size(1.dp).focusRequester(focusRequester),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword)
                )

                OtpInputRow(otpValue = uiState.otp, otpLength = 4, onOtpChange = { viewModel.onOtpChanged(it) })

                AnimatedVisibility(visible = uiState.error != null) {
                    ErrorMessage(message = uiState.error ?: "")
                }

                RiderPrimaryButton(
                    text = "Verify & Complete",
                    onClick = { viewModel.verifyOtp() },
                    isLoading = uiState.isLoading,
                    enabled = uiState.otp.length == 4,
                    color = GbGreen
                )

                TextButton(
                    onClick = { viewModel.skipOtp() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Customer not available? Skip OTP", color = GbOnSurfaceDim, fontSize = 13.sp)
                }
            }

            Spacer(Modifier.height(32.dp))
        }

        LoadingOverlay(isVisible = uiState.isLoading)
    }
}
