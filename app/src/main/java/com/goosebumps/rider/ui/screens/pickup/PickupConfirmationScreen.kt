package com.goosebumps.rider.ui.screens.pickup

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.goosebumps.rider.domain.model.OrderItem
import com.goosebumps.rider.ui.components.*
import com.goosebumps.rider.ui.theme.*

@Composable
fun PickupConfirmationScreen(
    orderId: String,
    onPickedUp: () -> Unit,
    viewModel: PickupConfirmationViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(orderId) { viewModel.loadOrder(orderId) }
    LaunchedEffect(uiState.pickedUp) { if (uiState.pickedUp) onPickedUp() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(GbBackground)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            RiderTopBar(title = "Pickup Confirmation")

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                // Restaurant info
                item {
                    RiderCard(modifier = Modifier.fillMaxWidth()) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Icon(Icons.Default.Restaurant, contentDescription = null, tint = GbOrange, modifier = Modifier.size(28.dp))
                            Column {
                                Text(uiState.restaurantName, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
                                Text("Order #${uiState.orderNumber}", fontSize = 12.sp, color = GbOnSurfaceDim)
                            }
                        }
                    }
                }

                // Items header
                item {
                    SectionHeader(title = "ORDER ITEMS (${uiState.items.size})")
                }

                // Items list
                items(uiState.items) { item ->
                    OrderItemRow(item = item)
                }

                // Waiting timer
                item {
                    WaitingTimerCard(
                        waitingSeconds = uiState.waitingSeconds,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // Delay report
                item {
                    RiderSecondaryButton(
                        text = "Report Delay",
                        onClick = { viewModel.reportDelay() },
                        color = GbYellow
                    )
                }
            }

            // Bottom confirm button
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(GbSurface)
                    .padding(16.dp)
            ) {
                RiderPrimaryButton(
                    text = "Confirm Pickup",
                    onClick = { viewModel.confirmPickup() },
                    isLoading = uiState.isLoading
                )
            }
        }

        LoadingOverlay(isVisible = uiState.isLoading)
    }
}

@Composable
private fun OrderItemRow(item: OrderItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(GbSurface)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Veg/Non-veg indicator
        Box(
            modifier = Modifier
                .size(16.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(if (item.isVeg) GbGreen else GbRed)
        )
        Text(
            text = item.name,
            modifier = Modifier.weight(1f),
            fontSize = 14.sp,
            color = Color.White
        )
        Text(
            text = "x${item.quantity}",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = GbOrange
        )
    }
}

@Composable
private fun WaitingTimerCard(waitingSeconds: Int, modifier: Modifier = Modifier) {
    val minutes = waitingSeconds / 60
    val seconds = waitingSeconds % 60
    val isLong = waitingSeconds > 300 // 5 min

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isLong) GbYellow.copy(alpha = 0.1f) else GbSurface
        ),
        border = if (isLong) androidx.compose.foundation.BorderStroke(1.dp, GbYellow.copy(alpha = 0.4f)) else null
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                Icons.Default.Timer,
                contentDescription = null,
                tint = if (isLong) GbYellow else GbOnSurfaceDim,
                modifier = Modifier.size(22.dp)
            )
            Column {
                Text(
                    text = "Waiting at restaurant",
                    fontSize = 12.sp,
                    color = GbOnSurfaceDim
                )
                Text(
                    text = "${String.format("%02d", minutes)}:${String.format("%02d", seconds)}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = if (isLong) GbYellow else Color.White
                )
            }
            if (isLong) {
                Spacer(Modifier.weight(1f))
                Text("Long wait!", fontSize = 12.sp, color = GbYellow, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}
