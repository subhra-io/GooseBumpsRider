package com.goosebumps.rider.ui.screens.order

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.goosebumps.rider.ui.map.GeoapifyMapView
import com.goosebumps.rider.ui.components.*
import com.goosebumps.rider.ui.theme.*

@Composable
fun IncomingOrderScreen(
    orderId: String,
    onAccepted: () -> Unit,
    onDeclined: () -> Unit,
    viewModel: IncomingOrderViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(orderId) { viewModel.loadOrder(orderId) }
    LaunchedEffect(uiState.accepted) { if (uiState.accepted) onAccepted() }
    LaunchedEffect(uiState.declined) { if (uiState.declined) onDeclined() }

    // Pulsing ring animation
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f, targetValue = 1.15f,
        animationSpec = infiniteRepeatable(tween(800), RepeatMode.Reverse),
        label = "pulse_scale"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(GbBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(48.dp))

            // Timer
            Box(contentAlignment = Alignment.Center) {
                CircularProgressIndicator(
                    progress = { uiState.timerProgress },
                    modifier = Modifier.size(100.dp),
                    color = if (uiState.timerSeconds <= 10) GbRed else GbOrange,
                    strokeWidth = 6.dp,
                    trackColor = GbSurface2
                )
                CountdownTimer(secondsRemaining = uiState.timerSeconds)
            }

            Spacer(Modifier.height(8.dp))
            Text("New Order Request", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)

            Spacer(Modifier.height(24.dp))

            // Order details card
            RiderCard(modifier = Modifier.fillMaxWidth()) {
                // Restaurant
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Box(
                        modifier = Modifier.size(48.dp).clip(RoundedCornerShape(12.dp)).background(GbOrange.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Restaurant, contentDescription = null, tint = GbOrange, modifier = Modifier.size(26.dp))
                    }
                    Column {
                        Text(uiState.restaurantName, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
                        Text("Pickup", fontSize = 12.sp, color = GbOnSurfaceDim)
                    }
                    Spacer(Modifier.weight(1f))
                    DistanceBadge(distanceKm = uiState.pickupDistanceKm)
                }

                Spacer(Modifier.height(12.dp))
                RiderDivider()
                Spacer(Modifier.height(12.dp))

                // Delivery
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Box(
                        modifier = Modifier.size(48.dp).clip(RoundedCornerShape(12.dp)).background(GbGreen.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.LocationOn, contentDescription = null, tint = GbGreen, modifier = Modifier.size(26.dp))
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(uiState.deliveryAddress, fontWeight = FontWeight.Medium, fontSize = 14.sp, color = Color.White, maxLines = 2)
                        Text("Delivery", fontSize = 12.sp, color = GbOnSurfaceDim)
                    }
                    DistanceBadge(distanceKm = uiState.deliveryDistanceKm)
                }

                Spacer(Modifier.height(16.dp))
                RiderDivider()
                Spacer(Modifier.height(16.dp))

                // Earnings row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    EarningsStat(label = "Earnings", value = "₹${String.format("%.0f", uiState.estimatedEarnings)}", color = GbOrange)
                    EarningsStat(label = "Time", value = "${uiState.estimatedMinutes} min", color = GbOnSurface)
                    EarningsStat(label = "Items", value = "${uiState.itemCount}", color = GbOnSurface)
                }

                if (uiState.surgeMultiplier > 1.0) {
                    Spacer(Modifier.height(12.dp))
                    SurgeIndicator(multiplier = uiState.surgeMultiplier, modifier = Modifier.fillMaxWidth())
                }
            }

            Spacer(Modifier.height(24.dp))

            // Map preview — Geoapify static view centered on restaurant
            if (uiState.restaurantLat != 0.0) {
                GeoapifyMapView(
                    lat = uiState.restaurantLat,
                    lng = uiState.restaurantLng,
                    zoom = 14,
                    markerLat = uiState.restaurantLat,
                    markerLng = uiState.restaurantLng,
                    markerLabel = uiState.restaurantName,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .clip(RoundedCornerShape(16.dp))
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(GbSurface2),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.Map, contentDescription = null, tint = GbOnSurfaceDim, modifier = Modifier.size(36.dp))
                        Text("Loading map...", fontSize = 12.sp, color = GbOnSurfaceDim)
                    }
                }
            }

            Spacer(Modifier.weight(1f))

            // Action buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                RiderSecondaryButton(
                    text = "Decline",
                    onClick = { viewModel.declineOrder() },
                    modifier = Modifier.weight(1f),
                    color = GbRed
                )
                RiderPrimaryButton(
                    text = "Accept",
                    onClick = { viewModel.acceptOrder() },
                    modifier = Modifier.weight(2f),
                    isLoading = uiState.isLoading
                )
            }
        }

        LoadingOverlay(isVisible = uiState.isLoading)
    }
}

@Composable
private fun EarningsStat(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = color)
        Text(label, fontSize = 11.sp, color = GbOnSurfaceDim)
    }
}
