package com.example.pulseup.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.models.Response
import com.example.domain.usecases.auth.LoginUseCase
import com.example.domain.usecases.auth.RegisterUseCase
import com.example.domain.usecases.auth.ResetPasswordUseCase
import com.google.firebase.auth.AuthResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthEmailViewModel @Inject constructor(
    private val registerUseCase: RegisterUseCase,
    private val loginUseCase: LoginUseCase,
    private val resetPasswordUseCase: ResetPasswordUseCase
): ViewModel() {

    private val _authFlow = MutableSharedFlow<Response<AuthResult>>()
    val authFlow = _authFlow

    private val _resetPasswordFlow = MutableSharedFlow<Response<Void?>>()
    val resetPasswordFlow = _resetPasswordFlow


    fun login(email: String, password: String) = viewModelScope.launch {
        loginUseCase.invoke(email, password).collect {
            _authFlow.emit(it)
        }
    }

    fun register(email: String, password: String) = viewModelScope.launch {
        registerUseCase.invoke(email, password).collect {
            _authFlow.emit(it)
        }
    }



    fun resetPassword(email: String) = viewModelScope.launch {
        resetPasswordUseCase.invoke(email).collect { response ->
            _resetPasswordFlow.emit(response)
        }
    }


}
