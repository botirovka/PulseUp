package com.example.data.repository

import com.example.domain.models.Response
import com.example.domain.repository.AuthenticationRepository
import com.example.domain.utils.FirebaseCollections.USER_EMAIL
import com.example.domain.utils.FirebaseCollections.USER_SIGNED_IN_GOOGLE
import com.example.domain.utils.FirebaseCollections.USER_SIGNED_IN_PASSWORD
import com.example.domain.utils.FirebaseCollections.USER_TABLE_NAME
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthenticationRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firebaseFirestore: FirebaseFirestore
) : AuthenticationRepository {

    override suspend fun userUid(): String = auth.currentUser?.uid ?: ""

    override suspend fun isLoggedIn(): Boolean = auth.currentUser == null

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

    override suspend fun resetPassword(email: String): Flow<Response<Void?>> = flow {
        try {
            emit(Response.Loading)
            val data = auth.sendPasswordResetEmail(email).await()
            emit(Response.Success(data))
        } catch (e: Exception) {
            emit(Response.Error(e.localizedMessage ?: "Oops, something went wrong."))
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

}