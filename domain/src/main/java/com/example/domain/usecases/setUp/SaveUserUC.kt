package com.example.domain.usecases.setUp

import com.example.domain.models.User
import com.example.domain.repository.UserRepository
import javax.inject.Inject

class SaveUserUC @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(user: User): Result<Unit> {
        return userRepository.saveUser(user)
    }
}