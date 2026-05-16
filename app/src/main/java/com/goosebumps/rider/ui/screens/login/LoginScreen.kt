package com.goosebumps.rider.ui.screens.login

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.goosebumps.rider.ui.components.*
import com.goosebumps.rider.ui.theme.*

@Composable
fun LoginScreen(
    onNavigateToOtp: (phone: String, countryCode: String) -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(uiState.navigateToOtp) {
        if (uiState.navigateToOtp) {
            keyboardController?.hide()
            onNavigateToOtp(uiState.phone, uiState.selectedCountryCode)
            viewModel.onNavigated()
        }
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
            // Header
            Column(modifier = Modifier.padding(top = 60.dp)) {
                Text(
                    text = "Welcome back,",
                    fontSize = 16.sp,
                    color = GbOnSurfaceDim
                )
                Text(
                    text = "Rider 🏍",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "Enter your mobile number to continue",
                    fontSize = 14.sp,
                    color = GbOnSurfaceDim
                )
            }

            // Input section
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                // Phone input with country code
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(GbSurface)
                        .border(
                            width = 1.dp,
                            color = if (uiState.phoneError != null) GbRed else GbDivider,
                            shape = RoundedCornerShape(16.dp)
                        ),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Country code picker
                    Row(
                        modifier = Modifier
                            .clickable { viewModel.toggleCountryPicker() }
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = uiState.selectedFlag,
                            fontSize = 20.sp
                        )
                        Text(
                            text = uiState.selectedCountryCode,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = GbOnSurface
                        )
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = null,
                            tint = GbOnSurfaceDim,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // Divider
                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(32.dp)
                            .background(GbDivider)
                    )

                    // Phone number field
                    BasicTextField(
                        value = uiState.phone,
                        onValueChange = { viewModel.onPhoneChanged(it.filter { c -> c.isDigit() }.take(10)) },
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 16.dp)
                            .focusRequester(focusRequester),
                        textStyle = androidx.compose.ui.text.TextStyle(
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium,
                            color = GbOnSurface
                        ),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Phone,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = { viewModel.sendOtp() }
                        ),
                        singleLine = true,
                        decorationBox = { innerTextField ->
                            if (uiState.phone.isEmpty()) {
                                Text(
                                    text = "Mobile number",
                                    fontSize = 16.sp,
                                    color = GbOnSurfaceDim
                                )
                            }
                            innerTextField()
                        }
                    )
                }

                // Error message
                AnimatedVisibility(visible = uiState.phoneError != null) {
                    Text(
                        text = uiState.phoneError ?: "",
                        color = GbRed,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }

                // Country picker dropdown
                AnimatedVisibility(
                    visible = uiState.showCountryPicker,
                    enter = expandVertically() + fadeIn(),
                    exit = shrinkVertically() + fadeOut()
                ) {
                    CountryPickerDropdown(
                        countries = viewModel.countries,
                        onSelect = { code, flag -> viewModel.selectCountry(code, flag) }
                    )
                }

                Spacer(Modifier.height(8.dp))

                RiderPrimaryButton(
                    text = "Continue",
                    onClick = { viewModel.sendOtp() },
                    isLoading = uiState.isLoading,
                    enabled = uiState.phone.length >= 10
                )

                Text(
                    text = "By continuing, you agree to our Terms of Service and Privacy Policy",
                    fontSize = 12.sp,
                    color = GbOnSurfaceDim,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            }

            Spacer(Modifier.height(32.dp))
        }

        LoadingOverlay(isVisible = uiState.isLoading)
    }
}

@Composable
private fun BasicTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    textStyle: androidx.compose.ui.text.TextStyle = androidx.compose.ui.text.TextStyle.Default,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = false,
    decorationBox: @Composable (innerTextField: @Composable () -> Unit) -> Unit = { it() }
) {
    androidx.compose.foundation.text.BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        textStyle = textStyle,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        singleLine = singleLine,
        decorationBox = decorationBox
    )
}

@Composable
private fun CountryPickerDropdown(
    countries: List<Pair<String, String>>, // code, flag
    onSelect: (String, String) -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = GbSurface2),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            countries.forEach { (code, flag) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSelect(code, flag) }
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = flag, fontSize = 20.sp)
                    Text(text = code, fontSize = 16.sp, color = GbOnSurface)
                }
                RiderDivider()
            }
        }
    }
}
