package com.goosebumps.rider.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.goosebumps.rider.ui.theme.*

// Large primary action button — rider-first, thumb-friendly
@Composable
fun RiderPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    color: Color = GbOrange
) {
    val scale by animateFloatAsState(
        targetValue = if (enabled) 1f else 0.97f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "btn_scale"
    )
    Button(
        onClick = onClick,
        enabled = enabled && !isLoading,
        modifier = modifier
            .fillMaxWidth()
            .height(60.dp)
            .scale(scale),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = color,
            disabledContainerColor = color.copy(alpha = 0.4f)
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = Color.White,
                strokeWidth = 2.dp
            )
        } else {
            Text(
                text = text,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

// Secondary outlined button
@Composable
fun RiderSecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    color: Color = GbOrange
) {
    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.fillMaxWidth().height(56.dp),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.5.dp, color),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = color)
    ) {
        Text(text = text, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
    }
}

// Card container
@Composable
fun RiderCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val cardModifier = if (onClick != null) {
        modifier.clickable(onClick = onClick)
    } else modifier

    Card(
        modifier = cardModifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = GbSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), content = content)
    }
}

// Top app bar for navigation screens
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RiderTopBar(
    title: String,
    onBack: (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {}
) {
    TopAppBar(
        title = {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = GbOnSurface
            )
        },
        navigationIcon = {
            if (onBack != null) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = GbOnSurface
                    )
                }
            }
        },
        actions = actions,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = GbBackground,
            titleContentColor = GbOnSurface
        )
    )
}

// Stat card for dashboard
@Composable
fun StatCard(
    label: String,
    value: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    valueColor: Color = GbOrange
) {
    RiderCard(modifier = modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(valueColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = valueColor, modifier = Modifier.size(22.dp))
            }
            Spacer(Modifier.width(12.dp))
            Column {
                Text(text = value, fontWeight = FontWeight.Bold, fontSize = 20.sp, color = valueColor)
                Text(text = label, fontSize = 12.sp, color = GbOnSurfaceDim)
            }
        }
    }
}

// Online/Offline toggle pill
@Composable
fun OnlineToggle(
    isOnline: Boolean,
    onToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val bgColor by animateColorAsState(
        targetValue = if (isOnline) GbGreen else GbSurface3,
        animationSpec = tween(300),
        label = "toggle_color"
    )
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(bgColor)
            .clickable { onToggle(!isOnline) }
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(if (isOnline) Color.White else GbOnSurfaceDim)
        )
        Text(
            text = if (isOnline) "ONLINE" else "OFFLINE",
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = if (isOnline) Color.White else GbOnSurfaceDim
        )
    }
}

// OTP input field row
@Composable
fun OtpInputRow(
    otpValue: String,
    otpLength: Int = 6,
    onOtpChange: (String) -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(otpLength) { index ->
            val char = otpValue.getOrNull(index)?.toString() ?: ""
            val isFocused = index == otpValue.length
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(GbSurface2)
                    .border(
                        width = if (isFocused) 2.dp else 1.dp,
                        color = if (isFocused) GbOrange else GbDivider,
                        shape = RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = char,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = GbOnSurface,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

// Loading overlay
@Composable
fun LoadingOverlay(isVisible: Boolean) {
    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.6f)),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = GbOrange, strokeWidth = 3.dp)
        }
    }
}

// Error snackbar message
@Composable
fun ErrorMessage(message: String, modifier: Modifier = Modifier) {
    if (message.isNotEmpty()) {
        Card(
            modifier = modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.error.copy(alpha = 0.15f))
        ) {
            Text(
                text = message,
                modifier = Modifier.padding(12.dp),
                color = MaterialTheme.colorScheme.error,
                fontSize = 14.sp
            )
        }
    }
}

// Countdown timer display
@Composable
fun CountdownTimer(
    secondsRemaining: Int,
    modifier: Modifier = Modifier,
    warningThreshold: Int = 10
) {
    val color = if (secondsRemaining <= warningThreshold) GbRed else GbOrange
    val scale by animateFloatAsState(
        targetValue = if (secondsRemaining <= warningThreshold) 1.1f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "timer_scale"
    )
    Text(
        text = "${secondsRemaining}s",
        modifier = modifier.scale(scale),
        fontSize = 28.sp,
        fontWeight = FontWeight.Bold,
        color = color
    )
}

// Earnings amount display
@Composable
fun EarningsAmount(
    amount: Double,
    label: String,
    modifier: Modifier = Modifier,
    large: Boolean = false
) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "₹${String.format("%.0f", amount)}",
            fontSize = if (large) 36.sp else 24.sp,
            fontWeight = FontWeight.Bold,
            color = GbOrange
        )
        Text(text = label, fontSize = 12.sp, color = GbOnSurfaceDim)
    }
}

// Distance badge
@Composable
fun DistanceBadge(distanceKm: Double, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(GbSurface3)
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = "${String.format("%.1f", distanceKm)} km",
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = GbOnSurface
        )
    }
}

// Surge indicator
@Composable
fun SurgeIndicator(multiplier: Double, modifier: Modifier = Modifier) {
    if (multiplier > 1.0) {
        Box(
            modifier = modifier
                .clip(RoundedCornerShape(8.dp))
                .background(GbYellow.copy(alpha = 0.2f))
                .padding(horizontal = 10.dp, vertical = 4.dp)
        ) {
            Text(
                text = "⚡ ${String.format("%.1f", multiplier)}x Surge",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = GbYellow
            )
        }
    }
}

// Section header
@Composable
fun SectionHeader(title: String, modifier: Modifier = Modifier) {
    Text(
        text = title,
        modifier = modifier,
        fontSize = 14.sp,
        fontWeight = FontWeight.SemiBold,
        color = GbOnSurfaceDim,
        letterSpacing = 0.5.sp
    )
}

// Divider
@Composable
fun RiderDivider(modifier: Modifier = Modifier) {
    HorizontalDivider(
        modifier = modifier,
        color = GbDivider,
        thickness = 1.dp
    )
}
