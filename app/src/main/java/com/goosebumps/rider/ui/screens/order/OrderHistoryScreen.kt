package com.goosebumps.rider.ui.screens.order

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
import com.goosebumps.rider.domain.model.Order
import com.goosebumps.rider.ui.components.*
import com.goosebumps.rider.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderHistoryScreen(
    onBack: () -> Unit,
    viewModel: OrderHistoryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val listState = rememberLazyListState()

    Scaffold(
        containerColor = GbBackground,
        topBar = {
            Column {
                RiderTopBar(title = "Order History", onBack = onBack)
                OutlinedTextField(
                    value = uiState.searchQuery,
                    onValueChange = { viewModel.onSearch(it) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    placeholder = { Text("Search orders...", color = GbOnSurfaceDim) },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = null,
                            tint = GbOnSurfaceDim
                        )
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GbOrange,
                        unfocusedBorderColor = GbDivider,
                        focusedTextColor = GbOnSurface,
                        unfocusedTextColor = GbOnSurface,
                        cursorColor = GbOrange
                    ),
                    singleLine = true
                )
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when {
                uiState.orders.isEmpty() && uiState.isLoading -> {
                    CircularProgressIndicator(
                        color = GbOrange,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                uiState.orders.isEmpty() -> {
                    EmptyHistoryState(modifier = Modifier.align(Alignment.Center))
                }

                else -> {
                    LazyColumn(
                        state = listState,
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        // Pull-to-refresh hint at top
                        item {
                            AnimatedVisibility(visible = uiState.isLoading) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(
                                        color = GbOrange,
                                        modifier = Modifier.size(24.dp),
                                        strokeWidth = 2.dp
                                    )
                                }
                            }
                        }

                        items(uiState.orders, key = { it.id }) { order ->
                            OrderHistoryCard(order = order)
                        }

                        // Infinite scroll trigger
                        if (uiState.hasMore) {
                            item {
                                LaunchedEffect(Unit) { viewModel.loadMore() }
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(
                                        color = GbOrange,
                                        modifier = Modifier.size(24.dp),
                                        strokeWidth = 2.dp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Refresh FAB — simple alternative to swipe-to-refresh
            FloatingActionButton(
                onClick = { viewModel.refresh() },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp),
                containerColor = GbSurface2,
                contentColor = GbOrange,
                elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Refresh",
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}

@Composable
private fun OrderHistoryCard(order: Order) {
    RiderCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(GbGreen.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = GbGreen,
                    modifier = Modifier.size(24.dp)
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    order.restaurant.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = Color.White
                )
                Text("#${order.orderNumber}", fontSize = 12.sp, color = GbOnSurfaceDim)
                Text(order.createdAt.take(10), fontSize = 11.sp, color = GbOnSurfaceDim)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    "₹${String.format("%.0f", order.estimatedEarnings)}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = GbOrange
                )
                Text(
                    "${String.format("%.1f", order.deliveryDistanceKm)} km",
                    fontSize = 11.sp,
                    color = GbOnSurfaceDim
                )
            }
        }
    }
}

@Composable
private fun EmptyHistoryState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("📦", fontSize = 48.sp)
        Text(
            "No orders yet",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.White
        )
        Text(
            "Your completed orders will appear here",
            fontSize = 13.sp,
            color = GbOnSurfaceDim
        )
    }
}
