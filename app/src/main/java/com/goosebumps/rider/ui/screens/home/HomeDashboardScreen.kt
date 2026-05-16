package com.goosebumps.rider.ui.screens.home

import androidx.compose.animation.*
import androidx.compose.foundation.*
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.goosebumps.rider.ui.components.*
import com.goosebumps.rider.ui.theme.*
import java.util.Calendar

@Composable
fun HomeDashboardScreen(
    onNavigateToIncomingOrder: (String) -> Unit,
    onNavigateToEarnings: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToProfile: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.incomingOrderId) {
        uiState.incomingOrderId?.let {
            onNavigateToIncomingOrder(it)
            viewModel.onOrderNavigated()
        }
    }

    Scaffold(
        containerColor = GbBackground,
        bottomBar = {
            RiderBottomNav(
                onEarnings = onNavigateToEarnings,
                onHistory = onNavigateToHistory,
                onProfile = onNavigateToProfile
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            HomeHeader(
                riderName = uiState.riderName,
                isOnline = uiState.isOnline,
                onToggleOnline = { viewModel.toggleOnlineStatus() }
            )

            Spacer(Modifier.height(16.dp))

            // Stats row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    label = "Today's Earnings",
                    value = "₹${String.format("%.0f", uiState.todayEarnings)}",
                    icon = Icons.Default.AccountBalanceWallet,
                    modifier = Modifier.weight(1f),
                    valueColor = GbOrange
                )
                StatCard(
                    label = "Completed",
                    value = "${uiState.ordersToday}",
                    icon = Icons.Default.CheckCircle,
                    modifier = Modifier.weight(1f),
                    valueColor = GbGreen
                )
            }

            Spacer(Modifier.height(12.dp))

            // Performance score
            PerformanceCard(
                score = uiState.performanceScore,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )

            Spacer(Modifier.height(12.dp))

            // Surge indicator
            if (uiState.surgeMultiplier > 1.0) {
                SurgeCard(
                    multiplier = uiState.surgeMultiplier,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                )
                Spacer(Modifier.height(12.dp))
            }

            // Active order card
            AnimatedVisibility(
                visible = uiState.hasActiveOrder,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                ActiveOrderCard(
                    orderNumber = uiState.activeOrderNumber,
                    status = uiState.activeOrderStatus,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                )
                Spacer(Modifier.height(12.dp))
            }

            // Demand heatmap placeholder
            DemandCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )

            // Fatigue indicator
            if (uiState.showFatigueAlert) {
                Spacer(Modifier.height(12.dp))
                FatigueAlertCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    onDismiss = { viewModel.dismissFatigueAlert() }
                )
            }

            Spacer(Modifier.height(80.dp))
        }
    }
}

@Composable
private fun HomeHeader(
    riderName: String,
    isOnline: Boolean,
    onToggleOnline: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(GbSurface, GbBackground)
                )
            )
            .padding(horizontal = 16.dp, vertical = 20.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = getGreeting(),
                        fontSize = 14.sp,
                        color = GbOnSurfaceDim
                    )
                    Text(
                        text = riderName.ifEmpty { "Rider" },
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                OnlineToggle(isOnline = isOnline, onToggle = { onToggleOnline() })
            }
        }
    }
}

@Composable
private fun PerformanceCard(score: Float, modifier: Modifier = Modifier) {
    RiderCard(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Performance Score", fontSize = 12.sp, color = GbOnSurfaceDim)
                Text(
                    text = "${String.format("%.1f", score)}/5.0",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = GbYellow
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                repeat(5) { i ->
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = if (i < score.toInt()) GbYellow else GbDivider,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun SurgeCard(multiplier: Double, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = GbYellow.copy(alpha = 0.1f)),
        border = BorderStroke(1.dp, GbYellow.copy(alpha = 0.4f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("⚡", fontSize = 24.sp)
            Column {
                Text("Surge Pricing Active", fontWeight = FontWeight.Bold, color = GbYellow, fontSize = 14.sp)
                Text("Earn ${String.format("%.1f", multiplier)}x on every delivery now!", fontSize = 12.sp, color = GbOnSurfaceDim)
            }
        }
    }
}

@Composable
private fun ActiveOrderCard(orderNumber: String, status: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = GbOrange.copy(alpha = 0.1f)),
        border = BorderStroke(1.dp, GbOrange.copy(alpha = 0.4f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(GbOrange.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.DeliveryDining, contentDescription = null, tint = GbOrange, modifier = Modifier.size(22.dp))
            }
            Column(modifier = Modifier.weight(1f)) {
                Text("Active Order #$orderNumber", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 14.sp)
                Text(status, fontSize = 12.sp, color = GbOrange)
            }
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = GbOnSurfaceDim)
        }
    }
}

@Composable
private fun DemandCard(modifier: Modifier = Modifier) {
    RiderCard(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Nearby Demand", fontSize = 12.sp, color = GbOnSurfaceDim)
                Text("High demand in your area", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
            }
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(GbGreen.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Text("🔥", fontSize = 22.sp)
            }
        }
        Spacer(Modifier.height(8.dp))
        // Heatmap placeholder bars
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            listOf(0.4f, 0.7f, 0.9f, 0.6f, 0.8f, 1.0f, 0.75f).forEach { height ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height((height * 32).dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(GbOrange, GbOrange.copy(alpha = 0.3f))
                            )
                        )
                )
            }
        }
    }
}

@Composable
private fun FatigueAlertCard(modifier: Modifier = Modifier, onDismiss: () -> Unit) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = GbRed.copy(alpha = 0.1f)),
        border = BorderStroke(1.dp, GbRed.copy(alpha = 0.4f))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("😴", fontSize = 24.sp)
            Column(modifier = Modifier.weight(1f)) {
                Text("Take a Break", fontWeight = FontWeight.Bold, color = GbRed, fontSize = 14.sp)
                Text("You've been riding for 4+ hours. Rest for 15 mins.", fontSize = 12.sp, color = GbOnSurfaceDim)
            }
            IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                Icon(Icons.Default.Close, contentDescription = "Dismiss", tint = GbOnSurfaceDim, modifier = Modifier.size(18.dp))
            }
        }
    }
}

@Composable
private fun RiderBottomNav(
    onEarnings: () -> Unit,
    onHistory: () -> Unit,
    onProfile: () -> Unit
) {
    NavigationBar(
        containerColor = GbSurface,
        tonalElevation = 0.dp
    ) {
        NavigationBarItem(
            selected = false,
            onClick = onEarnings,
            icon = { Icon(Icons.Default.AccountBalanceWallet, contentDescription = "Earnings") },
            label = { Text("Earnings", fontSize = 11.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = GbOrange,
                unselectedIconColor = GbOnSurfaceDim,
                indicatorColor = GbOrange.copy(alpha = 0.15f)
            )
        )
        NavigationBarItem(
            selected = false,
            onClick = onHistory,
            icon = { Icon(Icons.Default.History, contentDescription = "History") },
            label = { Text("History", fontSize = 11.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = GbOrange,
                unselectedIconColor = GbOnSurfaceDim,
                indicatorColor = GbOrange.copy(alpha = 0.15f)
            )
        )
        NavigationBarItem(
            selected = false,
            onClick = onProfile,
            icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
            label = { Text("Profile", fontSize = 11.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = GbOrange,
                unselectedIconColor = GbOnSurfaceDim,
                indicatorColor = GbOrange.copy(alpha = 0.15f)
            )
        )
    }
}

private fun getGreeting(): String {
    return when (Calendar.getInstance().get(Calendar.HOUR_OF_DAY)) {
        in 5..11 -> "Good morning,"
        in 12..16 -> "Good afternoon,"
        in 17..20 -> "Good evening,"
        else -> "Good night,"
    }
}
