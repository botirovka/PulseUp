package com.example.pulseup

import android.util.Patterns
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.fragment.app.Fragment
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.GoogleAuthProvider

fun String.validateEmail(): Boolean {
    val email = this
    val emailPattern = Patterns.EMAIL_ADDRESS
    return emailPattern.matcher(email).matches()
}

fun String.validatePassword(): Boolean {
    return this.length >= 8
}

fun Int.formatTime(): String {
    val hours = this / 3600
    val minutes = (this % 3600) / 60
    val secs = this % 60
    return String.format("%02d:%02d", minutes, secs)
}

suspend fun Fragment.getGoogleCredentials(credentialManager: CredentialManager): AuthCredential {

    val getCredentialRequest = GetCredentialRequest.Builder().addCredentialOption(
        GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(getString(R.string.default_web_client_id))
            .build()
    ).build()

    val credentialResponse = credentialManager.getCredential(
        request = getCredentialRequest,
        context = requireActivity()
    )

    val googleCredential = GoogleIdTokenCredential.createFrom(credentialResponse.credential.data)
    val firebaseCredential = GoogleAuthProvider.getCredential(googleCredential.idToken, null)
    return firebaseCredential
}