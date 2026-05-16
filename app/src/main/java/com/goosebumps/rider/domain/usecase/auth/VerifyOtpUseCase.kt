package com.goosebumps.rider.domain.usecase.auth

import com.goosebumps.rider.domain.model.Rider
import com.goosebumps.rider.domain.repository.AuthRepository
import com.goosebumps.rider.domain.util.Result
import javax.inject.Inject

class VerifyOtpUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(phone: String, countryCode: String, otp: String): Result<Rider> {
        if (otp.length != 6) return Result.Error("Enter the 6-digit OTP")
        return authRepository.verifyOtp(phone, countryCode, otp)
    }
}
