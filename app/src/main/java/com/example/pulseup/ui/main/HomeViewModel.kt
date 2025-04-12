package com.example.pulseup.ui.main

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.models.Response
import com.example.domain.models.User
import com.example.domain.models.Workout
import com.example.domain.usecases.main.GetWorkoutsUC
import com.example.domain.usecases.setUp.GetUserInfoUC
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getUserInfoUC: GetUserInfoUC,
    private val getWorkoutsUC: GetWorkoutsUC
): ViewModel() {

    var workouts = emptyList<Workout>()

    private val _loadUserInfoFlow = MutableSharedFlow<Result<User>>()
    val loadUserInfoFlow = _loadUserInfoFlow

    private val _loadWorkoutsFlow = MutableSharedFlow<Response<List<Workout>>>()
    val loadWorkoutsFlow = _loadWorkoutsFlow

    fun loadUserData() {
        viewModelScope.launch {
            _loadUserInfoFlow.emit(getUserInfoUC())
        }
    }

    fun loadWorkouts() {
        viewModelScope.launch {
            getWorkoutsUC().collect{
                Log.d("mydebug", "loadWorkouts: $it")
                _loadWorkoutsFlow.emit(it)
            }
        }
    }
}