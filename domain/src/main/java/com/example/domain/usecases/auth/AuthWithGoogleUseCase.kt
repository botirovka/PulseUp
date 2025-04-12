package com.example.domain.usecases.auth

import com.example.domain.repository.AuthenticationRepository
import com.google.firebase.auth.AuthCredential
import javax.inject.Inject

class AuthWithGoogleUseCase @Inject constructor(
    private val authenticationRepository: AuthenticationRepository
) {
    suspend operator fun invoke(googleCredential: AuthCredential, email: String = "") =
        authenticationRepository.authWithGoogle(googleCredential, email)
}