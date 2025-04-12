package com.example.pulseup.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.models.Exercise
import com.example.domain.models.Response
import com.example.domain.models.Workout
import com.example.domain.usecases.main.GetExercisesByIdsUC
import com.example.domain.usecases.main.GetWorkoutByIdUC
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WorkoutViewModel @Inject constructor(
    private val getExercisesByIdsUC: GetExercisesByIdsUC,
    private val getWorkoutByIdUC: GetWorkoutByIdUC
): ViewModel() {

    var exercises = emptyList<Exercise>()

    private val _loadWorkoutFlow = MutableSharedFlow<Response<Workout>>()
    val loadWorkoutFlow  = _loadWorkoutFlow

    private val _loadExercisesFlow = MutableSharedFlow<Response<List<Exercise>>>()
    val loadExercisesFlow  = _loadExercisesFlow

    fun loadWorkout(workoutId: String) {
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
}