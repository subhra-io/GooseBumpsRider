package com.goosebumps.rider.domain.usecase.auth

import com.goosebumps.rider.domain.repository.AuthRepository
import com.goosebumps.rider.domain.util.Result
import javax.inject.Inject

class SendOtpUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(phone: String, countryCode: String): Result<Unit> {
        if (phone.length < 10) return Result.Error("Enter a valid phone number")
        return authRepository.sendOtp(phone, countryCode)
    }
}
