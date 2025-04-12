package com.example.domain.repository

import com.example.domain.models.Exercise
import com.example.domain.models.Response
import com.example.domain.models.UserDto
import com.example.domain.models.Workout
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import kotlinx.coroutines.flow.Flow

interface AuthenticationRepository {
    suspend fun login(email: String, password: String): Flow<Response<AuthResult>>

    suspend fun register(email: String, password: String): Flow<Response<AuthResult>>

    suspend fun authWithGoogle(googleCredential: AuthCredential, email: String): Flow<Response<AuthResult>>

    suspend fun resetPassword(email: String): Flow<Response<Void?>>
    suspend fun uploadInfo(userDto: UserDto): Flow<Response<Void?>>
    suspend fun getUserInfo(): Result<UserDto>

    suspend fun logout()

    suspend fun userUid(): String

    suspend fun isUserLoggedIn(): Boolean
    suspend fun getWorkouts(): Flow<Response<List<Workout>>>
    suspend fun getExercisesByIds(exerciseIds: List<String>): Flow<Response<List<Exercise>>>
    suspend fun getWorkoutById(workoutId: String): Flow<Response<Workout>>
}