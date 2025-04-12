package com.example.domain.usecases.main

import com.example.domain.repository.AuthenticationRepository
import javax.inject.Inject

class GetWorkoutByIdUC @Inject constructor(
    private val authenticationRepository: AuthenticationRepository
) {
    suspend operator fun invoke(workoutId: String)
    = authenticationRepository.getWorkoutById(workoutId)
}