package com.goosebumps.rider.ui.screens.earnings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.goosebumps.rider.domain.model.DailyEarning
import com.goosebumps.rider.domain.repository.RiderRepository
import com.goosebumps.rider.domain.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class EarningsUiState(
    val today: Double = 0.0,
    val thisWeek: Double = 0.0,
    val thisMonth: Double = 0.0,
    val ordersToday: Int = 0,
    val incentives: Double = 0.0,
    val bonuses: Double = 0.0,
    val fuelEstimate: Double = 0.0,
    val performanceScore: Float = 0f,
    val weeklyData: List<DailyEarning> = emptyList(),
    val monthlyData: List<DailyEarning> = emptyList(),
    val isLoading: Boolean = false
)

@HiltViewModel
class EarningsViewModel @Inject constructor(
    private val riderRepository: RiderRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(EarningsUiState())
    val uiState: StateFlow<EarningsUiState> = _uiState

    init {
        loadEarnings()
    }

    private fun loadEarnings() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            when (val result = riderRepository.getEarnings()) {
                is Result.Success -> {
                    val e = result.data
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            today = e.today,
                            thisWeek = e.thisWeek,
                            thisMonth = e.thisMonth,
                            ordersToday = e.ordersToday,
                            incentives = e.incentives,
                            bonuses = e.bonuses,
                            fuelEstimate = e.fuelEstimate,
                            performanceScore = e.performanceScore,
                            weeklyData = e.weeklyData,
                            monthlyData = e.monthlyData
                        )
                    }
                }
                is Result.Error -> _uiState.update { it.copy(isLoading = false) }
                else -> {}
            }
        }
    }
}
