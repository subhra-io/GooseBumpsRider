package com.goosebumps.rider.ui.screens.pickup

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
fun PickupNavigationScreen(
    orderId: String,
    onArrived: () -> Unit,
    viewModel: PickupNavigationViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(orderId) { viewModel.loadOrder(orderId) }
    LaunchedEffect(uiState.arrived) { if (uiState.arrived) onArrived() }

    Box(modifier = Modifier.fillMaxSize().background(GbBackground)) {

        // Geoapify map — shows route from rider to restaurant
        GeoapifyMapView(
            lat = if (uiState.restaurantLat != 0.0) uiState.restaurantLat else 12.9716,
            lng = if (uiState.restaurantLng != 0.0) uiState.restaurantLng else 77.5946,
            zoom = 14,
            markerLat = uiState.restaurantLat.takeIf { it != 0.0 },
            markerLng = uiState.restaurantLng.takeIf { it != 0.0 },
            markerLabel = uiState.restaurantName,
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
                    Text("Heading to Pickup", fontSize = 12.sp, color = GbOnSurfaceDim)
                    Text(
                        uiState.restaurantName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color.White
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("ETA", fontSize = 11.sp, color = GbOnSurfaceDim)
                    Text(
                        "${uiState.etaMinutes} min",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = GbOrange
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

        // Bottom action panel
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                .background(GbSurface)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Restaurant info row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(GbOrange.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Restaurant,
                        contentDescription = null,
                        tint = GbOrange,
                        modifier = Modifier.size(26.dp)
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        uiState.restaurantName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color.White
                    )
                    Text(
                        uiState.restaurantAddress,
                        fontSize = 12.sp,
                        color = GbOnSurfaceDim,
                        maxLines = 1
                    )
                }
                // Call restaurant
                IconButton(
                    onClick = { viewModel.callRestaurant() },
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(GbGreen.copy(alpha = 0.15f))
                ) {
                    Icon(
                        Icons.Default.Phone,
                        contentDescription = "Call restaurant",
                        tint = GbGreen,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            RiderDivider()

            // Traffic indicator
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Traffic,
                    contentDescription = null,
                    tint = GbYellow,
                    modifier = Modifier.size(16.dp)
                )
                Text("Moderate traffic on route", fontSize = 12.sp, color = GbOnSurfaceDim)
            }

            // Action buttons
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                RiderSecondaryButton(
                    text = "Navigate",
                    onClick = { viewModel.openNavigation() },
                    modifier = Modifier.weight(1f)
                )
                RiderPrimaryButton(
                    text = "Arrived",
                    onClick = { viewModel.markArrived() },
                    modifier = Modifier.weight(1f),
                    isLoading = uiState.isLoading
                )
            }
        }
    }
}
