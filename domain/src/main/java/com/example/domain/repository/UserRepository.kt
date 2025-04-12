package com.example.domain.repository

import com.example.domain.models.User

interface UserRepository {
    suspend fun saveUser(user: User): Result<Unit>
    suspend fun getUser(): Result<User?>
    suspend fun clearUser(): Result<Unit>
}