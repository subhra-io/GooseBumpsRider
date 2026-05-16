package com.goosebumps.rider.data.repository

import com.goosebumps.rider.data.local.db.RiderProfileDao
import com.goosebumps.rider.data.local.entity.RiderProfileEntity
import com.goosebumps.rider.data.local.prefs.SessionManager
import com.goosebumps.rider.data.remote.api.RiderApiService
import com.goosebumps.rider.data.remote.dto.LocationUpdateRequest
import com.goosebumps.rider.data.remote.dto.RiderStatusRequest
import com.goosebumps.rider.domain.model.*
import com.goosebumps.rider.domain.repository.RiderRepository
import com.goosebumps.rider.domain.util.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class RiderRepositoryImpl @Inject constructor(
    private val api: RiderApiService,
    private val profileDao: RiderProfileDao,
    private val sessionManager: SessionManager
) : RiderRepository {

    override fun getProfile(): Flow<Rider?> {
        return profileDao.getProfile().map { it?.toDomain() }
    }

    override suspend fun fetchProfile(): Result<Rider> {
        return try {
            val response = api.getProfile()
            if (response.isSuccessful) {
                val dto = response.body()?.data ?: return Result.Error("Profile not found")
                val rider = Rider(
                    id = dto.id, name = dto.name, phone = dto.phone,
                    profileImage = dto.profileImage, vehicleType = dto.vehicleType,
                    vehicleNumber = dto.vehicleNumber, rating = dto.rating,
                    totalDeliveries = dto.totalDeliveries, walletBalance = dto.walletBalance,
                    isOnline = dto.isOnline, language = dto.language
                )
                profileDao.insertProfile(rider.toEntity())
                Result.Success(rider)
            } else Result.Error("Failed to load profile")
        } catch (e: Exception) {
            Result.Error(e.message ?: "Network error")
        }
    }

    override suspend fun updateOnlineStatus(isOnline: Boolean): Result<Rider> {
        return try {
            val response = api.updateStatus(RiderStatusRequest(isOnline))
            if (response.isSuccessful) {
                val dto = response.body()?.data ?: return Result.Error("Update failed")
                sessionManager.setOnlineStatus(isOnline)
                val rider = Rider(
                    id = dto.id, name = dto.name, phone = dto.phone,
                    profileImage = dto.profileImage, vehicleType = dto.vehicleType,
                    vehicleNumber = dto.vehicleNumber, rating = dto.rating,
                    totalDeliveries = dto.totalDeliveries, walletBalance = dto.walletBalance,
                    isOnline = dto.isOnline, language = dto.language
                )
                profileDao.insertProfile(rider.toEntity())
                Result.Success(rider)
            } else Result.Error("Failed to update status")
        } catch (e: Exception) {
            sessionManager.setOnlineStatus(isOnline)
            Result.Error(e.message ?: "Network error")
        }
    }

    override suspend fun updateLocation(location: RiderLocation): Result<Unit> {
        return try {
            val response = api.updateLocation(
                LocationUpdateRequest(
                    lat = location.lat, lng = location.lng,
                    speed = location.speed, battery = location.battery,
                    accuracy = location.accuracy
                )
            )
            if (response.isSuccessful) Result.Success(Unit)
            else Result.Error("Location update failed")
        } catch (e: Exception) {
            Result.Error(e.message ?: "Network error")
        }
    }

    override suspend fun getEarnings(): Result<Earnings> {
        return try {
            val response = api.getEarnings()
            if (response.isSuccessful) {
                val dto = response.body()?.data ?: return Result.Error("No earnings data")
                Result.Success(
                    Earnings(
                        today = dto.today, thisWeek = dto.thisWeek, thisMonth = dto.thisMonth,
                        ordersToday = dto.ordersToday, ordersWeek = dto.ordersWeek,
                        ordersMonth = dto.ordersMonth, incentives = dto.incentives,
                        bonuses = dto.bonuses, fuelEstimate = dto.fuelEstimate,
                        performanceScore = dto.performanceScore,
                        weeklyData = dto.weeklyData.map { DailyEarning(it.date, it.amount, it.orders) },
                        monthlyData = dto.monthlyData.map { DailyEarning(it.date, it.amount, it.orders) }
                    )
                )
            } else Result.Error("Failed to load earnings")
        } catch (e: Exception) {
            Result.Error(e.message ?: "Network error")
        }
    }

    override suspend fun updateFcmToken(token: String): Result<Unit> {
        return try {
            val response = api.updateFcmToken(mapOf("fcm_token" to token))
            if (response.isSuccessful) Result.Success(Unit)
            else Result.Error("Token update failed")
        } catch (e: Exception) {
            Result.Error(e.message ?: "Network error")
        }
    }

    private fun RiderProfileEntity.toDomain() = Rider(
        id = id, name = name, phone = phone, profileImage = profileImage,
        vehicleType = vehicleType, vehicleNumber = vehicleNumber, rating = rating,
        totalDeliveries = totalDeliveries, walletBalance = walletBalance,
        isOnline = isOnline, language = language
    )

    private fun Rider.toEntity() = RiderProfileEntity(
        id = id, name = name, phone = phone, profileImage = profileImage,
        vehicleType = vehicleType, vehicleNumber = vehicleNumber, rating = rating,
        totalDeliveries = totalDeliveries, walletBalance = walletBalance,
        isOnline = isOnline, language = language
    )
}
