package com.goosebumps.rider.ui.screens.splash

import androidx.lifecycle.ViewModel
import com.goosebumps.rider.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    authRepository: AuthRepository
) : ViewModel() {
    val isLoggedIn: StateFlow<Boolean> = MutableStateFlow(authRepository.isLoggedIn())
}
