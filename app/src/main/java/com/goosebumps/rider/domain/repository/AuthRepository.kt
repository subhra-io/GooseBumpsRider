package com.goosebumps.rider.domain.repository

import com.goosebumps.rider.domain.model.Rider
import com.goosebumps.rider.domain.util.Result

interface AuthRepository {
    suspend fun sendOtp(phone: String, countryCode: String): Result<Unit>
    suspend fun verifyOtp(phone: String, countryCode: String, otp: String): Result<Rider>
    suspend fun logout(): Result<Unit>
    fun isLoggedIn(): Boolean
}
