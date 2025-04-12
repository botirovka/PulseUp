package com.example.domain.usecases.setUp

import com.example.domain.mappers.toUserDto
import com.example.domain.models.User
import com.example.domain.repository.AuthenticationRepository
import javax.inject.Inject

class UploadUserInfoUC @Inject constructor(
    private val authenticationRepository: AuthenticationRepository
) {
    suspend operator fun invoke(user: User) =
        authenticationRepository.uploadInfo(user.toUserDto())
}