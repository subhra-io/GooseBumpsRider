package com.goosebumps.rider.data.repository

import com.goosebumps.rider.data.local.db.RiderProfileDao
import com.goosebumps.rider.data.local.entity.RiderProfileEntity
import com.goosebumps.rider.data.local.prefs.SessionManager
import com.goosebumps.rider.data.remote.api.RiderApiService
import com.goosebumps.rider.data.remote.dto.SendOtpRequest
import com.goosebumps.rider.data.remote.dto.VerifyOtpRequest
import com.goosebumps.rider.domain.model.Rider
import com.goosebumps.rider.domain.repository.AuthRepository
import com.goosebumps.rider.domain.util.Result
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val api: RiderApiService,
    private val sessionManager: SessionManager,
    private val profileDao: RiderProfileDao
) : AuthRepository {

    override suspend fun sendOtp(phone: String, countryCode: String): Result<Unit> {
        return try {
            val response = api.sendOtp(SendOtpRequest(phone, countryCode))
            if (response.isSuccessful) Result.Success(Unit)
            else Result.Error(response.errorBody()?.string() ?: "Failed to send OTP")
        } catch (e: Exception) {
            Result.Error(e.message ?: "Network error")
        }
    }

    override suspend fun verifyOtp(phone: String, countryCode: String, otp: String): Result<Rider> {
        return try {
            val response = api.verifyOtp(VerifyOtpRequest(phone, countryCode, otp))
            if (response.isSuccessful) {
                val body = response.body()
                val token = body?.token
                val riderDto = body?.rider
                if (token != null && riderDto != null) {
                    sessionManager.saveSession(token, riderDto.id, riderDto.name)
                    val rider = Rider(
                        id = riderDto.id,
                        name = riderDto.name,
                        phone = riderDto.phone,
                        profileImage = riderDto.profileImage,
                        vehicleType = riderDto.vehicleType,
                        vehicleNumber = riderDto.vehicleNumber,
                        rating = riderDto.rating,
                        totalDeliveries = riderDto.totalDeliveries,
                        walletBalance = riderDto.walletBalance,
                        isOnline = riderDto.isOnline,
                        language = riderDto.language
                    )
                    profileDao.insertProfile(
                        RiderProfileEntity(
                            id = rider.id,
                            name = rider.name,
                            phone = rider.phone,
                            profileImage = rider.profileImage,
                            vehicleType = rider.vehicleType,
                            vehicleNumber = rider.vehicleNumber,
                            rating = rider.rating,
                            totalDeliveries = rider.totalDeliveries,
                            walletBalance = rider.walletBalance,
                            isOnline = rider.isOnline,
                            language = rider.language
                        )
                    )
                    Result.Success(rider)
                } else {
                    Result.Error(body?.message ?: "Invalid OTP")
                }
            } else {
                Result.Error("Invalid OTP. Please try again.")
            }
        } catch (e: Exception) {
            Result.Error(e.message ?: "Network error")
        }
    }

    override suspend fun logout(): Result<Unit> {
        return try {
            api.logout()
            sessionManager.clearSession()
            profileDao.clearProfile()
            Result.Success(Unit)
        } catch (e: Exception) {
            sessionManager.clearSession()
            profileDao.clearProfile()
            Result.Success(Unit)
        }
    }

    override fun isLoggedIn(): Boolean = sessionManager.isLoggedIn()
}
