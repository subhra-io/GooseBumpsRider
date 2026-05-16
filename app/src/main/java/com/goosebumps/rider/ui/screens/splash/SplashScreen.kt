package com.goosebumps.rider.ui.screens.splash

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.goosebumps.rider.ui.theme.GbBackground
import com.goosebumps.rider.ui.theme.GbOrange
import com.goosebumps.rider.ui.theme.GbOrangeDim
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToHome: () -> Unit,
    viewModel: SplashViewModel = hiltViewModel()
) {
    val isLoggedIn by viewModel.isLoggedIn.collectAsState()

    // Logo animation
    var logoVisible by remember { mutableStateOf(false) }
    var taglineVisible by remember { mutableStateOf(false) }

    val logoScale by animateFloatAsState(
        targetValue = if (logoVisible) 1f else 0.3f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "logo_scale"
    )

    val logoAlpha by animateFloatAsState(
        targetValue = if (logoVisible) 1f else 0f,
        animationSpec = tween(600),
        label = "logo_alpha"
    )

    LaunchedEffect(Unit) {
        delay(200)
        logoVisible = true
        delay(500)
        taglineVisible = true
        delay(1200)
        if (isLoggedIn) onNavigateToHome() else onNavigateToLogin()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(GbBackground),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo circle
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .scale(logoScale)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(GbOrange, GbOrangeDim)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "🏍",
                    fontSize = 52.sp
                )
            }

            Spacer(Modifier.height(24.dp))

            // App name
            AnimatedVisibility(
                visible = logoVisible,
                enter = fadeIn(tween(600)) + slideInVertically { it / 2 }
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "GOOSEBUMPS",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        letterSpacing = 4.sp
                    )
                    Text(
                        text = "RIDER",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = GbOrange,
                        letterSpacing = 8.sp
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // Tagline
            AnimatedVisibility(
                visible = taglineVisible,
                enter = fadeIn(tween(500)) + slideInVertically { it }
            ) {
                Text(
                    text = "Deliver. Earn. Repeat.",
                    fontSize = 14.sp,
                    color = Color.White.copy(alpha = 0.5f),
                    fontWeight = FontWeight.Light
                )
            }
        }

        // Bottom loading dots
        AnimatedVisibility(
            visible = taglineVisible,
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 60.dp),
            enter = fadeIn()
        ) {
            LoadingDots()
        }
    }
}

@Composable
private fun LoadingDots() {
    val infiniteTransition = rememberInfiniteTransition(label = "dots")
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        repeat(3) { index ->
            val alpha by infiniteTransition.animateFloat(
                initialValue = 0.2f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(600, delayMillis = index * 200),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "dot_$index"
            )
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(GbOrange.copy(alpha = alpha))
            )
        }
    }
}
