package com.example.pulseup.ui.setUp

import android.util.Log
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.models.Genders
import com.example.domain.models.Goals
import com.example.domain.models.Levels
import com.example.domain.models.Response
import com.example.domain.models.User
import com.example.domain.usecases.auth.IsUserLoggedInUseCase
import com.example.domain.usecases.auth.LogoutUseCase
import com.example.domain.usecases.setUp.ClearUserUC
import com.example.domain.usecases.setUp.GetUserInfoUC
import com.example.domain.usecases.setUp.GetUserUC
import com.example.domain.usecases.setUp.SaveUserUC
import com.example.domain.usecases.setUp.UploadUserInfoUC
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SetUpViewModel @Inject constructor(
    private val saveUserUC: SaveUserUC,
    private val getUserUC: GetUserUC,
    private val clearUserUC: ClearUserUC,
    private val isUserLoggedInUseCase: IsUserLoggedInUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val uploadUserInfoUC: UploadUserInfoUC,
    private val getUserInfoUC: GetUserInfoUC
): ViewModel(), DefaultLifecycleObserver {

    private val _userState = MutableStateFlow(User())
    val userState: StateFlow<User> = _userState

    private val _isLoggedInState = MutableStateFlow<Boolean?>(null)
    val isLoggedInState: StateFlow<Boolean?> = _isLoggedInState

    private val _uploadUserInfoFlow = MutableSharedFlow<Response<Void?>>()
    val uploadUserInfoFlow = _uploadUserInfoFlow

    private val _loadUserInfoFlow = MutableSharedFlow<Result<User>>()
    val loadUserInfoFlow = _loadUserInfoFlow


    init {
        checkLoginStatus()
        loadUser()
    }


    fun setFullName(fullName: String) {
        _userState.value = _userState.value.copy(fullName = fullName)
    }

    fun setNickName(nickName: String) {
        _userState.value = _userState.value.copy(nickName = nickName)
    }

    fun setGender(gender: Genders) {
        _userState.value = _userState.value.copy(gender = gender)
        Log.d("mydebug", "setGender: ${_userState.value.gender}")
    }

    fun setAge(age: Int) {
        _userState.value = _userState.value.copy(age = age)
    }

    fun setWeight(weight: Int) {
        _userState.value = _userState.value.copy(weight = weight)
    }

    fun setHeight(height: Int) {
        _userState.value = _userState.value.copy(height = height)
    }

    fun setGoal(goal: Goals) {
        _userState.value = _userState.value.copy(goal = goal)
    }

    fun setLevel(level: Levels) {
        _userState.value = _userState.value.copy(level = level)
    }

    fun saveUser() {
        viewModelScope.launch {
            val currentUser = _userState.value
            saveUserUC(currentUser)
                .onSuccess {
                    println("User saved successfully!")
                }
                .onFailure { error ->
                    println("Error saving user: ${error.message}")
                    error.printStackTrace()
                }
        }
    }

    fun loadUser() {
        viewModelScope.launch {
            getUserUC()
                .onSuccess { user ->
                    user?.let { loadedUser ->
                        _userState.value = loadedUser
                    } ?: run {
                        println("No user found, using default values.")
                    }
                }
                .onFailure { error ->
                    println("Error loading user: ${error.message}")
                    error.printStackTrace()
                }
        }
    }

    fun uploadUserData() {
        viewModelScope.launch {
        loadUser()
            uploadUserInfoUC(userState.value).collectLatest { response ->
                _uploadUserInfoFlow.emit(response)
            }
        }
    }


    fun checkLoginStatus() {
        viewModelScope.launch {
            val isLoggedIn = isUserLoggedInUseCase()
            _isLoggedInState.value = isLoggedIn
            Log.d("mydebug", "checkLoginStatus: ${_isLoggedInState.value}")
        }
    }

    fun loadUserData() {
        viewModelScope.launch {
            val user = getUserInfoUC()
            Log.d("mydebug", "loadUserData: $user")
            _loadUserInfoFlow.emit(user)
        }
    }

    fun logout(){
        viewModelScope.launch {
            logoutUseCase()
        }
    }

}