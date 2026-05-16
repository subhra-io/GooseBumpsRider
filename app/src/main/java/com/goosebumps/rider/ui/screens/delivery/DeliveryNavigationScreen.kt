package com.goosebumps.rider.ui.screens.delivery

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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
import com.goosebumps.rider.ui.components.*
import com.goosebumps.rider.ui.map.GeoapifyMapView
import com.goosebumps.rider.ui.theme.*

@Composable
fun DeliveryNavigationScreen(
    orderId: String,
    onDelivered: () -> Unit,
    viewModel: DeliveryNavigationViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(orderId) { viewModel.loadOrder(orderId) }
    LaunchedEffect(uiState.delivered) { if (uiState.delivered) onDelivered() }

    Box(modifier = Modifier.fillMaxSize().background(GbBackground)) {

        // Geoapify map — shows route to customer
        GeoapifyMapView(
            lat = if (uiState.deliveryLat != 0.0) uiState.deliveryLat else 12.9716,
            lng = if (uiState.deliveryLng != 0.0) uiState.deliveryLng else 77.5946,
            zoom = 14,
            markerLat = uiState.deliveryLat.takeIf { it != 0.0 },
            markerLng = uiState.deliveryLng.takeIf { it != 0.0 },
            markerLabel = uiState.customerName,
            routePoints = uiState.routePoints,
            modifier = Modifier.fillMaxSize()
        )

        // Top ETA card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .align(Alignment.TopCenter),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = GbSurface.copy(alpha = 0.95f))
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Delivering to", fontSize = 12.sp, color = GbOnSurfaceDim)
                    Text(
                        uiState.customerName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color.White
                    )
                    Text(
                        uiState.deliveryAddress,
                        fontSize = 12.sp,
                        color = GbOnSurfaceDim,
                        maxLines = 1
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("ETA", fontSize = 11.sp, color = GbOnSurfaceDim)
                    Text(
                        "${uiState.etaMinutes} min",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = GbGreen
                    )
                    if (uiState.distanceKm > 0) {
                        Text(
                            "${String.format("%.1f", uiState.distanceKm)} km",
                            fontSize = 11.sp,
                            color = GbOnSurfaceDim
                        )
                    }
                }
            }
        }

        // Bottom panel
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                .background(GbSurface)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Customer info
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(GbGreen.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = null,
                        tint = GbGreen,
                        modifier = Modifier.size(26.dp)
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        uiState.customerName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color.White
                    )
                    Text(
                        uiState.deliveryAddress,
                        fontSize = 12.sp,
                        color = GbOnSurfaceDim,
                        maxLines = 1
                    )
                }
                IconButton(
                    onClick = { viewModel.callCustomer() },
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(GbGreen.copy(alpha = 0.15f))
                ) {
                    Icon(
                        Icons.Default.Phone,
                        contentDescription = "Call customer",
                        tint = GbGreen,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            // Special instructions
            if (uiState.specialInstructions.isNotEmpty()) {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = GbSurface2)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            Icons.Default.Info,
                            contentDescription = null,
                            tint = GbYellow,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(uiState.specialInstructions, fontSize = 12.sp, color = GbOnSurface)
                    }
                }
            }

            // Safety alert
            if (uiState.showSafetyAlert) {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = GbRed.copy(alpha = 0.1f))
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Warning,
                            contentDescription = null,
                            tint = GbRed,
                            modifier = Modifier.size(16.dp)
                        )
                        Text("Caution: Low-lit area ahead", fontSize = 12.sp, color = GbRed)
                    }
                }
            }

            RiderDivider()

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                RiderSecondaryButton(
                    text = "Navigate",
                    onClick = { viewModel.openNavigation() },
                    modifier = Modifier.weight(1f)
                )
                RiderPrimaryButton(
                    text = "Delivered",
                    onClick = { viewModel.markDelivered() },
                    modifier = Modifier.weight(1f),
                    color = GbGreen,
                    isLoading = uiState.isLoading
                )
            }
        }
    }
}
