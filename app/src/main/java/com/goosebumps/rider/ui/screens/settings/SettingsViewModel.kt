package com.goosebumps.rider.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.goosebumps.rider.data.local.prefs.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val voiceNavEnabled: Boolean = true,
    val batterySaverEnabled: Boolean = false,
    val notificationsEnabled: Boolean = true,
    val language: String = "en"
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState

    init {
        viewModelScope.launch {
            combine(
                sessionManager.voiceNavFlow,
                sessionManager.batterySaverFlow,
                sessionManager.languageFlow
            ) { voiceNav, batterySaver, language ->
                SettingsUiState(
                    voiceNavEnabled = voiceNav,
                    batterySaverEnabled = batterySaver,
                    language = language
                )
            }.collect { state -> _uiState.value = state }
        }
    }

    fun setVoiceNav(enabled: Boolean) {
        viewModelScope.launch { sessionManager.setVoiceNav(enabled) }
    }

    fun setBatterySaver(enabled: Boolean) {
        viewModelScope.launch { sessionManager.setBatterySaver(enabled) }
    }

    fun setNotifications(enabled: Boolean) {
        _uiState.update { it.copy(notificationsEnabled = enabled) }
    }

    fun setLanguage(lang: String) {
        viewModelScope.launch { sessionManager.setLanguage(lang) }
    }
}
