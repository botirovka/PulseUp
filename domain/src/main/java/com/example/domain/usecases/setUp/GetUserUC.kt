package com.example.domain.usecases.setUp

import com.example.domain.repository.UserRepository
import javax.inject.Inject

class GetUserUC @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke() =
        userRepository.getUser()
}