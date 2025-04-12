package com.example.domain.usecases.setUp

import com.example.domain.mappers.toUser
import com.example.domain.repository.AuthenticationRepository
import javax.inject.Inject

class GetUserInfoUC @Inject constructor(
    private val authenticationRepository: AuthenticationRepository
) {
    suspend operator fun invoke() =
        authenticationRepository.getUserInfo().map { userDto ->
            userDto.toUser()
        }
}