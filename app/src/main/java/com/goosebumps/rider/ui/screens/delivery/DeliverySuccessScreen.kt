package com.goosebumps.rider.ui.screens.delivery

import androidx.compose.animation.*
import androidx.compose.animation.core.*
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.goosebumps.rider.ui.components.*
import com.goosebumps.rider.ui.theme.*

@Composable
fun DeliverySuccessScreen(
    orderId: String,
    onNextOrder: () -> Unit,
    viewModel: DeliverySuccessViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(orderId) { viewModel.loadOrder(orderId) }

    // Success animation
    var showContent by remember { mutableStateOf(false) }
    val checkScale by animateFloatAsState(
        targetValue = if (showContent) 1f else 0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium),
        label = "check_scale"
    )

    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(200)
        showContent = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(GbBackground),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(Modifier.height(48.dp))

            // Success icon
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .scale(checkScale)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(GbGreen, GbGreen.copy(alpha = 0.6f))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(64.dp)
                    )
                }

                Spacer(Modifier.height(24.dp))

                AnimatedVisibility(
                    visible = showContent,
                    enter = fadeIn(tween(600, delayMillis = 300)) + slideInVertically { it / 2 }
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Delivered!",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = getMotivationalMessage(),
                            fontSize = 14.sp,
                            color = GbOnSurfaceDim,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            // Stats
            AnimatedVisibility(
                visible = showContent,
                enter = fadeIn(tween(600, delayMillis = 500)) + expandVertically()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(GbSurface)
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Earnings highlight
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        EarningsAmount(
                            amount = uiState.earnings,
                            label = "Earned",
                            large = true
                        )
                        if (uiState.tipAmount > 0) {
                            Spacer(Modifier.width(24.dp))
                            EarningsAmount(
                                amount = uiState.tipAmount,
                                label = "Tip 🎉",
                                large = false
                            )
                        }
                    }

                    RiderDivider()

                    // Trip stats
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        TripStat(icon = "📍", value = "${String.format("%.1f", uiState.distanceKm)} km", label = "Distance")
                        TripStat(icon = "⏱", value = "${uiState.timeTakenMinutes} min", label = "Time")
                        TripStat(icon = "⭐", value = "${uiState.rating}", label = "Rating")
                    }
                }
            }

            // CTA
            AnimatedVisibility(
                visible = showContent,
                enter = fadeIn(tween(600, delayMillis = 700)) + slideInVertically { it }
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    RiderPrimaryButton(
                        text = "Find Next Order",
                        onClick = onNextOrder
                    )
                    TextButton(
                        onClick = onNextOrder,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Go to Dashboard", color = GbOnSurfaceDim)
                    }
                }
            }
        }
    }
}

@Composable
private fun TripStat(icon: String, value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(icon, fontSize = 22.sp)
        Spacer(Modifier.height(4.dp))
        Text(value, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
        Text(label, fontSize = 11.sp, color = GbOnSurfaceDim)
    }
}

private fun getMotivationalMessage(): String {
    val messages = listOf(
        "Great job! Keep it up 🚀",
        "You're on fire today! 🔥",
        "Another one done. You're crushing it!",
        "Excellent delivery! Riders like you make the difference.",
        "Keep going, you're doing amazing! 💪"
    )
    return messages.random()
}
