package com.example.pulseup.ui.main.workout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.models.Exercise
import com.example.domain.models.Response
import com.example.domain.models.User
import com.example.domain.models.Workout
import com.example.domain.usecases.main.GetExercisesByIdsUC
import com.example.domain.usecases.main.GetWorkoutByIdUC
import com.example.domain.usecases.setUp.GetUserInfoUC
import com.example.domain.usecases.setUp.UploadUserInfoUC
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WorkoutViewModel @Inject constructor(
    private val getExercisesByIdsUC: GetExercisesByIdsUC,
    private val getWorkoutByIdUC: GetWorkoutByIdUC,
    private val uploadUserInfoUC: UploadUserInfoUC,
    private val getUserInfoUC: GetUserInfoUC
): ViewModel() {

    var exercises = emptyList<Exercise>()
    var workoutId: String = ""

    private val _loadUserInfoFlow = MutableSharedFlow<Result<User>>()
    val loadUserInfoFlow = _loadUserInfoFlow

    private val _loadWorkoutFlow = MutableSharedFlow<Response<Workout>>()
    val loadWorkoutFlow  = _loadWorkoutFlow

    private val _loadExercisesFlow = MutableSharedFlow<Response<List<Exercise>>>()
    val loadExercisesFlow  = _loadExercisesFlow

    private val _uploadUserInfoFlow = MutableSharedFlow<Response<Void?>>()
    val uploadUserInfoFlow = _uploadUserInfoFlow

    fun loadUserData() {
        viewModelScope.launch {
            _loadUserInfoFlow.emit(getUserInfoUC())
        }
    }

    fun loadWorkout(workoutId: String) {
        this.workoutId = workoutId
        viewModelScope.launch {
            getWorkoutByIdUC(workoutId).collect{ response ->
                _loadWorkoutFlow.emit(response)
            }
        }
    }

    fun loadExercises(exercisesIds: List<String>) {
        viewModelScope.launch {
            getExercisesByIdsUC(exercisesIds).collect{
                _loadExercisesFlow.emit(it)
            }
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