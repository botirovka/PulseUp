package com.example.pulseup.ui.main.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.models.Response
import com.example.domain.models.User
import com.example.domain.usecases.auth.LogoutUseCase
import com.example.domain.usecases.setUp.GetUserInfoUC
import com.example.domain.usecases.setUp.UploadUserInfoUC
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val logoutUseCase: LogoutUseCase,
    private val uploadUserInfoUC: UploadUserInfoUC,
    private val getUserInfoUC: GetUserInfoUC
): ViewModel() {
    var user = User()
    private val _loadUserInfoFlow = MutableSharedFlow<Result<User>>()
    val loadUserInfoFlow = _loadUserInfoFlow

    private val _uploadUserInfoFlow = MutableSharedFlow<Response<Void?>>()
    val uploadUserInfoFlow = _uploadUserInfoFlow

    fun logOut(){
        viewModelScope.launch {
            logoutUseCase()
        }
    }

    fun loadUserData() {
        viewModelScope.launch {
            _loadUserInfoFlow.emit(getUserInfoUC())
        }
    }

    fun uploadUserData(user: User) {
        viewModelScope.launch {
            uploadUserInfoUC(user).collectLatest { response ->
                _uploadUserInfoFlow.emit(response)
            }
        }
    }
}