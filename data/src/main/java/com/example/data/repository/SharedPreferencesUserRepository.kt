package com.example.data.repository

import android.content.Context
import com.example.data.sharedPreferences.SharedPreferencesManager
import com.example.domain.models.User
import com.example.domain.repository.UserRepository

class SharedPreferencesUserRepository(context: Context) : UserRepository {

    private val sharedPreferencesManager = SharedPreferencesManager(context)

    override suspend fun saveUser(user: User): Result<Unit> {
        return try {
            sharedPreferencesManager.saveUser(user)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getUser(): Result<User?> {
        return try {
            val user = sharedPreferencesManager.getUser()
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun clearUser(): Result<Unit> {
        return try {
            sharedPreferencesManager.clearUser()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}