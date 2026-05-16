package com.goosebumps.rider.ui.screens.earnings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.goosebumps.rider.domain.model.DailyEarning
import com.goosebumps.rider.ui.components.*
import com.goosebumps.rider.ui.theme.*

@Composable
fun EarningsScreen(
    onBack: () -> Unit,
    viewModel: EarningsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        containerColor = GbBackground,
        topBar = { RiderTopBar(title = "Earnings", onBack = onBack) }
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

            // Today's earnings hero
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = GbOrange.copy(alpha = 0.15f))
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Today's Earnings", fontSize = 14.sp, color = GbOnSurfaceDim)
                    Text(
                        text = "₹${String.format("%.0f", uiState.today)}",
                        fontSize = 48.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = GbOrange
                    )
                    Spacer(Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                        MiniStat("${uiState.ordersToday}", "Orders")
                        MiniStat("₹${String.format("%.0f", uiState.incentives)}", "Incentives")
                        MiniStat("₹${String.format("%.0f", uiState.bonuses)}", "Bonus")
                    }
                }
            }

            // Weekly / Monthly tabs
            var selectedTab by remember { mutableIntStateOf(0) }
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = GbSurface,
                contentColor = GbOrange,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = GbOrange
                    )
                }
            ) {
                Tab(selected = selectedTab == 0, onClick = { selectedTab = 0 }) {
                    Text("Weekly", modifier = Modifier.padding(vertical = 12.dp), fontSize = 14.sp)
                }
                Tab(selected = selectedTab == 1, onClick = { selectedTab = 1 }) {
                    Text("Monthly", modifier = Modifier.padding(vertical = 12.dp), fontSize = 14.sp)
                }
            }

            // Bar chart
            val chartData = if (selectedTab == 0) uiState.weeklyData else uiState.monthlyData
            EarningsBarChart(data = chartData, modifier = Modifier.fillMaxWidth())

            // Summary cards
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    label = if (selectedTab == 0) "This Week" else "This Month",
                    value = "₹${String.format("%.0f", if (selectedTab == 0) uiState.thisWeek else uiState.thisMonth)}",
                    icon = Icons.Default.TrendingUp,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    label = "Fuel Est.",
                    value = "₹${String.format("%.0f", uiState.fuelEstimate)}",
                    icon = Icons.Default.LocalGasStation,
                    modifier = Modifier.weight(1f),
                    valueColor = GbYellow
                )
            }

            // Performance
            RiderCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Performance Score", fontSize = 12.sp, color = GbOnSurfaceDim)
                        Text(
                            "${String.format("%.1f", uiState.performanceScore)}/5.0",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = GbYellow
                        )
                    }
                    LinearProgressIndicator(
                        progress = { uiState.performanceScore / 5f },
                        modifier = Modifier.width(120.dp).height(8.dp).clip(RoundedCornerShape(4.dp)),
                        color = GbYellow,
                        trackColor = GbSurface2
                    )
                }
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun MiniStat(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
        Text(label, fontSize = 11.sp, color = GbOnSurfaceDim)
    }
}

@Composable
private fun EarningsBarChart(data: List<DailyEarning>, modifier: Modifier = Modifier) {
    if (data.isEmpty()) return
    val maxAmount = data.maxOf { it.amount }.coerceAtLeast(1.0)

    RiderCard(modifier = modifier) {
        Column {
            Text("Earnings Trend", fontSize = 13.sp, color = GbOnSurfaceDim)
            Spacer(Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth().height(100.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                data.forEach { day ->
                    val heightFraction = (day.amount / maxAmount).toFloat()
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Bottom
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight(heightFraction)
                                .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                .background(GbOrange.copy(alpha = 0.7f + heightFraction * 0.3f))
                        )
                    }
                }
            }
            Spacer(Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                data.forEach { day ->
                    Text(
                        text = day.date.takeLast(2),
                        modifier = Modifier.weight(1f),
                        fontSize = 9.sp,
                        color = GbOnSurfaceDim,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        }
    }
}

// Extension for tab indicator
private fun Modifier.tabIndicatorOffset(tabPosition: TabPosition): Modifier = this.then(
    Modifier.fillMaxWidth(1f / 2f)
        .wrapContentSize(Alignment.BottomStart)
        .offset(x = tabPosition.left)
        .width(tabPosition.width)
)
