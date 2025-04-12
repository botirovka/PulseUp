package com.example.domain.usecases.main

import com.example.domain.repository.AuthenticationRepository
import javax.inject.Inject

class GetWorkoutsUC @Inject constructor(
    private val authenticationRepository: AuthenticationRepository
) {
    suspend operator fun invoke() = authenticationRepository.getWorkouts()
}