package com.goosebumps.rider.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.goosebumps.rider.ui.components.*
import com.goosebumps.rider.ui.theme.*

@Composable
fun ProfileScreen(
    onBack: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onLogout: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showLogoutDialog by remember { mutableStateOf(false) }

    Scaffold(
        containerColor = GbBackground,
        topBar = { RiderTopBar(title = "Profile", onBack = onBack) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(Modifier.height(8.dp))

            // Profile header
            RiderCard(modifier = Modifier.fillMaxWidth()) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(GbOrange.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = uiState.name.firstOrNull()?.toString() ?: "R",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = GbOrange
                        )
                    }
                    Spacer(Modifier.height(12.dp))
                    Text(uiState.name, fontWeight = FontWeight.Bold, fontSize = 20.sp, color = Color.White)
                    Text(uiState.phone, fontSize = 14.sp, color = GbOnSurfaceDim)
                    Spacer(Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Star, contentDescription = null, tint = GbYellow, modifier = Modifier.size(16.dp))
                        Text(
                            "${String.format("%.1f", uiState.rating)} rating",
                            fontSize = 13.sp,
                            color = GbYellow,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            // Stats
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    label = "Deliveries",
                    value = "${uiState.totalDeliveries}",
                    icon = Icons.Default.DeliveryDining,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    label = "Wallet",
                    value = "₹${String.format("%.0f", uiState.walletBalance)}",
                    icon = Icons.Default.AccountBalanceWallet,
                    modifier = Modifier.weight(1f),
                    valueColor = GbGreen
                )
            }

            // Vehicle info
            RiderCard(modifier = Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Icon(Icons.Default.TwoWheeler, contentDescription = null, tint = GbOrange, modifier = Modifier.size(28.dp))
                    Column {
                        Text(uiState.vehicleType, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = Color.White)
                        Text(uiState.vehicleNumber, fontSize = 13.sp, color = GbOnSurfaceDim)
                    }
                }
            }

            // Menu items
            SectionHeader(title = "ACCOUNT")
            ProfileMenuItem(icon = Icons.Default.Settings, label = "Settings", onClick = onNavigateToSettings)
            ProfileMenuItem(icon = Icons.Default.Language, label = "Language", onClick = {})
            ProfileMenuItem(icon = Icons.Default.Notifications, label = "Notifications", onClick = {})
            ProfileMenuItem(icon = Icons.Default.Help, label = "Help & Support", onClick = {})

            // Emergency SOS
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = GbRed.copy(alpha = 0.1f)),
                border = androidx.compose.foundation.BorderStroke(1.dp, GbRed.copy(alpha = 0.4f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(Icons.Default.Emergency, contentDescription = null, tint = GbRed, modifier = Modifier.size(28.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Emergency SOS", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = GbRed)
                        Text("Tap to alert emergency contacts", fontSize = 12.sp, color = GbOnSurfaceDim)
                    }
                    Button(
                        onClick = { viewModel.triggerSOS() },
                        colors = ButtonDefaults.buttonColors(containerColor = GbRed),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text("SOS", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }

            // Logout
            RiderSecondaryButton(
                text = "Logout",
                onClick = { showLogoutDialog = true },
                color = GbRed
            )

            Spacer(Modifier.height(16.dp))
        }
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Logout", color = Color.White) },
            text = { Text("Are you sure you want to logout?", color = GbOnSurfaceDim) },
            confirmButton = {
                TextButton(onClick = {
                    showLogoutDialog = false
                    viewModel.logout()
                    onLogout()
                }) {
                    Text("Logout", color = GbRed)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancel", color = GbOnSurfaceDim)
                }
            },
            containerColor = GbSurface
        )
    }
}

@Composable
private fun ProfileMenuItem(icon: ImageVector, label: String, onClick: () -> Unit) {
    RiderCard(modifier = Modifier.fillMaxWidth(), onClick = onClick) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(icon, contentDescription = null, tint = GbOnSurfaceDim, modifier = Modifier.size(22.dp))
            Text(label, fontSize = 15.sp, color = Color.White, modifier = Modifier.weight(1f))
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = GbOnSurfaceDim, modifier = Modifier.size(20.dp))
        }
    }
}
