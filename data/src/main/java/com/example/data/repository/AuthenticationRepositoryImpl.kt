package com.example.data.repository

import android.util.Log
import com.example.domain.models.Exercise
import com.example.domain.models.Response
import com.example.domain.models.UserDto
import com.example.domain.models.Workout
import com.example.domain.repository.AuthenticationRepository
import com.example.domain.utils.FirebaseCollections.USER_AGE
import com.example.domain.utils.FirebaseCollections.USER_EMAIL
import com.example.domain.utils.FirebaseCollections.USER_FULL_NAME
import com.example.domain.utils.FirebaseCollections.USER_GENDER
import com.example.domain.utils.FirebaseCollections.USER_GOAL
import com.example.domain.utils.FirebaseCollections.USER_HEIGHT
import com.example.domain.utils.FirebaseCollections.USER_HISTORY
import com.example.domain.utils.FirebaseCollections.USER_LEVEL
import com.example.domain.utils.FirebaseCollections.USER_NICKNAME
import com.example.domain.utils.FirebaseCollections.USER_SIGNED_IN_GOOGLE
import com.example.domain.utils.FirebaseCollections.USER_SIGNED_IN_PASSWORD
import com.example.domain.utils.FirebaseCollections.USER_TABLE_NAME
import com.example.domain.utils.FirebaseCollections.USER_WEIGHT
import com.example.domain.utils.FirebaseCollections.USER_WORKOUT_CALORIES
import com.example.domain.utils.FirebaseCollections.USER_WORKOUT_COUNT
import com.example.domain.utils.FirebaseCollections.USER_WORKOUT_DURATION
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthenticationRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firebaseFirestore: FirebaseFirestore
) : AuthenticationRepository {

    override suspend fun userUid(): String = auth.currentUser?.uid ?: ""

    override suspend fun isUserLoggedIn(): Boolean = auth.currentUser != null

    override suspend fun logout() = auth.signOut()

    override suspend fun login(email: String, password: String): Flow<Response<AuthResult>> = flow {
        try {
            emit(Response.Loading)
            val data = auth.signInWithEmailAndPassword(email, password).await()
            emit(Response.Success(data))
        } catch (e: Exception) {
            if (e is FirebaseAuthInvalidCredentialsException) {
                val providers = getUserProvidersByEmail(email)
                emit(
                    when {
                        providers.contains(USER_SIGNED_IN_PASSWORD) -> Response.WrongPassword
                        providers.contains(USER_SIGNED_IN_GOOGLE) -> Response.LinkToGoogle
                        else -> Response.EmailNotFound
                    }
                )
            }
            else emit(Response.Error(e.localizedMessage ?: "Oops, something went wrong."))

        }
    }

    override suspend fun register(email: String, password: String): Flow<Response<AuthResult>> =
        flow {
            try {
                emit(Response.Loading)
                val data = auth.createUserWithEmailAndPassword(email, password).await().apply {
                    user?.let { saveUserProvider(it.uid, it.email!!) }
                }
                emit(Response.Success(data))
            } catch (e: Exception) {
                if (e is FirebaseAuthUserCollisionException) {
                    val providers = getUserProvidersByEmail(email)
                    emit(
                        when {
                            providers.contains(USER_SIGNED_IN_PASSWORD) -> Response.EmailAlreadyInUse
                            providers.contains(USER_SIGNED_IN_GOOGLE) -> Response.LinkToGoogle
                            else -> Response.EmailNotFound
                        }
                    )
                }
                else emit(Response.Error(e.localizedMessage ?: "Oops, something went wrong."))
            }
        }

    override suspend fun authWithGoogle(
        googleCredential: AuthCredential,
        email: String
    ): Flow<Response<AuthResult>> =
        flow {
            try {
                emit(Response.Loading)
                val result = auth.signInWithCredential(googleCredential).await()
                if(email.isNotBlank()){
                    if(result.user?.email == email){
                        result.user?.let { saveUserProvider(it.uid, it.email!!, true) }
                        emit(Response.Success(result))
                    }
                    else{
                        emit(Response.WrongGoogleToLink)
                    }
                }
                else {
                    result.user?.let { saveUserProvider(it.uid, it.email!!, true) }
                    emit(Response.Success(result))
                }
            } catch (e: Exception) {
                emit(Response.Error(e.localizedMessage ?: "Oops, something went wrong."))
            }
        }

    override suspend fun resetPassword(email: String): Flow<Response<Void?>> = flow {
        try {
            emit(Response.Loading)
            val data = auth.sendPasswordResetEmail(email).await()
            emit(Response.Success(data))
        } catch (e: Exception) {
            emit(Response.Error(e.localizedMessage ?: "Oops, something went wrong."))
        }
    }

    override suspend fun uploadInfo(userDto: UserDto): Flow<Response<Void?>> = flow {
         try { emit(Response.Loading)
            userUid().let { userId ->
                val data = firebaseFirestore.collection(USER_TABLE_NAME)
                    .document(userId)
                    .update(
                        mapOf(
                            USER_FULL_NAME to userDto.fullName,
                            USER_NICKNAME to userDto.nickName,
                            USER_GENDER to userDto.gender,
                            USER_AGE to userDto.age,
                            USER_WEIGHT to userDto.weight,
                            USER_HEIGHT to userDto.height,
                            USER_GOAL to userDto.goal,
                            USER_LEVEL to userDto.level,
                            USER_HISTORY to userDto.historyWorkouts,
                            USER_WORKOUT_COUNT to userDto.totalWorkoutCount,
                            USER_WORKOUT_DURATION to userDto.totalWorkoutDuration,
                            USER_WORKOUT_CALORIES to userDto.totalWorkoutCalories

                        )
                    )
                    .await()
                emit(Response.Success(data))
            }

        } catch (e: Exception) {
             emit(Response.Error(e.localizedMessage ?: "Oops, something went wrong."))
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun getUserInfo(): Result<UserDto> {
        return try {
            val userId = userUid()
            val document = firebaseFirestore.collection(USER_TABLE_NAME)
                .document(userId)
                .get()
                .await()

            if (document.exists()) {
                val userDto = document.toObject(UserDto::class.java)
                userDto?.let { Result.success(it) } ?: Result.failure(Exception("User data is null"))
            } else {
                Result.failure(Exception("User not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun getUserData(email: String): Map<String, Any> =
        getUserSnapshot(email)?.data.orEmpty()

    private suspend fun getUserProvidersByEmail(email: String): List<String> {
        val userData = getUserData(email)
        return listOf(
            USER_SIGNED_IN_GOOGLE.takeIf { userData.containsKey(USER_SIGNED_IN_GOOGLE) },
            USER_SIGNED_IN_PASSWORD.takeIf { userData.containsKey(USER_SIGNED_IN_PASSWORD) }
        ).filterNotNull()
    }

    private suspend fun saveUserProvider(
        userId: String,
        email: String,
        isFromGoogle: Boolean = false
    ) {
        val providerKey = if (isFromGoogle) USER_SIGNED_IN_GOOGLE else USER_SIGNED_IN_PASSWORD
        val userData = mapOf(
            USER_EMAIL to email,
            providerKey to true
        )
        firebaseFirestore.collection(USER_TABLE_NAME)
            .document(userId)
            .set(userData, SetOptions.merge())
            .await()
    }

    private suspend fun getUserSnapshot(email: String): DocumentSnapshot? =
        firebaseFirestore.collection(USER_TABLE_NAME)
            .whereEqualTo(USER_EMAIL, email)
            .get()
            .await()
            .documents
            .firstOrNull()


    //workout part

    override suspend fun getWorkouts(): Flow<Response<List<Workout>>> = flow {
        try {
            emit(Response.Loading)
            val snapshot = firebaseFirestore.collection("workoutsBeginner").get().await()
            val workouts = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Workout::class.java)?.apply { id = doc.id }
            }
            Log.d("mydebug", "getWorkouts: $workouts")
            emit(Response.Success(workouts))
        } catch (e: Exception) {
            Log.d("mydebug", "getWorkouts: ${e.message}")
            emit(Response.Error(e.localizedMessage ?: "Oops, something went wrong."))
        }
    }

    override suspend fun getWorkoutById(workoutId: String): Flow<Response<Workout>> = flow {
        try {
            emit(Response.Loading)
            val workoutDocumentRef = firebaseFirestore.collection("workoutsBeginner").document(workoutId)
            val documentSnapshot = workoutDocumentRef.get().await()
            if (documentSnapshot.exists()) {
                val workout = documentSnapshot.toObject(Workout::class.java).also {
                    it?.id = workoutId
                }
                Log.d("mydebug", "getWorkout by ID: $workout")

                if (workout != null) {
                    emit(Response.Success(workout))
                } else {
                    emit(Response.Error("Failed to convert document to Workout object."))
                }
            }
            else{
                emit(Response.Error("Workout with ID '$workoutId' not found."))
            }
        } catch (e: Exception) {
            Log.d("mydebug", "getWorkouts: ${e.message}")
            emit(Response.Error(e.localizedMessage ?: "Oops, something went wrong."))
        }
    }

    override suspend fun getExercisesByIds(exerciseIds: List<String>): Flow<Response<List<Exercise>>> = flow {
        try {
            emit(Response.Loading)

            if (exerciseIds.isEmpty()) {
                emit(Response.Success(emptyList()))
                return@flow
            }

            val snapshot = firebaseFirestore.collection("exercises")
                .whereIn("__name__", exerciseIds)
                .get()
                .await()

            val exercises = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Exercise::class.java)
            }
            Log.d("mydebug", "getEx: $exercises")
            emit(Response.Success(exercises))
        } catch (e: Exception) {
            Log.d("mydebug", "getEx: ${e.message}")
            emit(Response.Error(e.localizedMessage ?: "Oops, something went wrong."))
        }
    }

}