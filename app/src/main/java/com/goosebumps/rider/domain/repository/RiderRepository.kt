package com.goosebumps.rider.domain.repository

import com.goosebumps.rider.domain.model.Earnings
import com.goosebumps.rider.domain.model.Rider
import com.goosebumps.rider.domain.model.RiderLocation
import com.goosebumps.rider.domain.util.Result
import kotlinx.coroutines.flow.Flow

interface RiderRepository {
    fun getProfile(): Flow<Rider?>
    suspend fun fetchProfile(): Result<Rider>
    suspend fun updateOnlineStatus(isOnline: Boolean): Result<Rider>
    suspend fun updateLocation(location: RiderLocation): Result<Unit>
    suspend fun getEarnings(): Result<Earnings>
    suspend fun updateFcmToken(token: String): Result<Unit>
}
