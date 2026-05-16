package com.goosebumps.rider.ui.screens.settings

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.goosebumps.rider.ui.components.*
import com.goosebumps.rider.ui.theme.*

@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        containerColor = GbBackground,
        topBar = { RiderTopBar(title = "Settings", onBack = onBack) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Spacer(Modifier.height(8.dp))

            SectionHeader(title = "PREFERENCES")

            ToggleSetting(
                icon = Icons.Default.DarkMode,
                label = "Dark Mode",
                description = "Always on for battery saving",
                checked = true,
                onCheckedChange = {},
                enabled = false
            )

            ToggleSetting(
                icon = Icons.Default.RecordVoiceOver,
                label = "Voice Navigation",
                description = "Audio turn-by-turn directions",
                checked = uiState.voiceNavEnabled,
                onCheckedChange = { viewModel.setVoiceNav(it) }
            )

            ToggleSetting(
                icon = Icons.Default.BatteryChargingFull,
                label = "Battery Saver Mode",
                description = "Reduces GPS frequency to save battery",
                checked = uiState.batterySaverEnabled,
                onCheckedChange = { viewModel.setBatterySaver(it) }
            )

            ToggleSetting(
                icon = Icons.Default.Notifications,
                label = "Push Notifications",
                description = "Order alerts and updates",
                checked = uiState.notificationsEnabled,
                onCheckedChange = { viewModel.setNotifications(it) }
            )

            Spacer(Modifier.height(4.dp))
            SectionHeader(title = "LANGUAGE")

            LanguageSelector(
                selectedLanguage = uiState.language,
                onLanguageSelected = { viewModel.setLanguage(it) }
            )

            Spacer(Modifier.height(4.dp))
            SectionHeader(title = "PRIVACY")

            SettingItem(icon = Icons.Default.PrivacyTip, label = "Privacy Policy", onClick = {})
            SettingItem(icon = Icons.Default.Description, label = "Terms of Service", onClick = {})
            SettingItem(icon = Icons.Default.DeleteForever, label = "Delete Account", onClick = {})

            Spacer(Modifier.height(4.dp))
            SectionHeader(title = "ABOUT")

            RiderCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("App Version", fontSize = 14.sp, color = Color.White)
                    Text("1.0.0", fontSize = 14.sp, color = GbOnSurfaceDim)
                }
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
private fun ToggleSetting(
    icon: ImageVector,
    label: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    enabled: Boolean = true
) {
    RiderCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(icon, contentDescription = null, tint = if (enabled) GbOrange else GbOnSurfaceDim, modifier = Modifier.size(22.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(label, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = if (enabled) Color.White else GbOnSurfaceDim)
                Text(description, fontSize = 12.sp, color = GbOnSurfaceDim)
            }
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                enabled = enabled,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = GbOrange,
                    uncheckedThumbColor = GbOnSurfaceDim,
                    uncheckedTrackColor = GbSurface3
                )
            )
        }
    }
}

@Composable
private fun LanguageSelector(selectedLanguage: String, onLanguageSelected: (String) -> Unit) {
    val languages = listOf(
        "en" to "English",
        "hi" to "हिंदी",
        "kn" to "ಕನ್ನಡ",
        "bn" to "বাংলা",
        "or" to "ଓଡ଼ିଆ"
    )

    RiderCard(modifier = Modifier.fillMaxWidth()) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            languages.forEach { (code, name) ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    RadioButton(
                        selected = selectedLanguage == code,
                        onClick = { onLanguageSelected(code) },
                        colors = RadioButtonDefaults.colors(selectedColor = GbOrange)
                    )
                    Text(name, fontSize = 14.sp, color = Color.White)
                }
            }
        }
    }
}

@Composable
private fun SettingItem(icon: ImageVector, label: String, onClick: () -> Unit) {
    RiderCard(modifier = Modifier.fillMaxWidth(), onClick = onClick) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(icon, contentDescription = null, tint = GbOnSurfaceDim, modifier = Modifier.size(22.dp))
            Text(label, fontSize = 14.sp, color = Color.White, modifier = Modifier.weight(1f))
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = GbOnSurfaceDim, modifier = Modifier.size(20.dp))
        }
    }
}
