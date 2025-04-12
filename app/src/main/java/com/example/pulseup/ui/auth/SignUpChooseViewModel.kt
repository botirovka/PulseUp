package com.example.pulseup.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.models.Response
import com.example.domain.usecases.auth.AuthWithGoogleUseCase
import com.example.domain.usecases.auth.IsUserLoggedInUseCase
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpChooseViewModel @Inject constructor(
    private val authWithGoogleUseCase: AuthWithGoogleUseCase,
    private val isUserLoggedInUseCase: IsUserLoggedInUseCase
): ViewModel() {

    private val _authFlow = MutableSharedFlow<Response<AuthResult>>()
    val authFlow = _authFlow

    private val _isLoggedInState = MutableStateFlow<Boolean?>(null)
    val isLoggedInState: StateFlow<Boolean?> = _isLoggedInState

    fun authWithGoogle(googleCredential: AuthCredential) = viewModelScope.launch {
        authWithGoogleUseCase(googleCredential).collect {
            _authFlow.emit(it)
        }
    }

}