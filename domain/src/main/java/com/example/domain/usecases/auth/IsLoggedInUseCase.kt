package com.example.domain.usecases.auth

import com.example.domain.repository.AuthenticationRepository
import javax.inject.Inject

class IsUserLoggedInUseCase @Inject constructor(
    private val authenticationRepository: AuthenticationRepository
) {
    suspend operator fun invoke(): Boolean {
        return authenticationRepository.isUserLoggedIn()
    }
}