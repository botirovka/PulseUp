package com.example.pulseup.ui.main

import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.domain.models.Exercise
import com.example.domain.models.Response
import com.example.pulseup.R
import com.example.pulseup.databinding.FragmentTrainingBinding
import com.example.pulseup.formatTime
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class TrainingFragment : Fragment() {
    private lateinit var binding: FragmentTrainingBinding
    private lateinit var countDownTimer: CountDownTimer
    private val viewModel: WorkoutViewModel by activityViewModels()
    private val args : TrainingFragmentArgs by navArgs()
    private val restTimeInSeconds = 10
    private var currentExercise = 0
    private var exerciseRepetions = mutableListOf<Int>()
    private var isTimerRunning = false
    private var timeLeftInMillis = 0L
    private var currentTimerType = TimerType.TIMER_TYPE_REST

    enum class TimerType {
        TIMER_TYPE_EXERCISE,
        TIMER_TYPE_REST
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTrainingBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.loadWorkout(args.workoutId)
        setupObservers()
        setupListeners()
    }

    private fun setupListeners() {
        binding.btnStartPause.setOnClickListener {
            if(isTimerRunning) {
                countDownTimer.cancel()
                isTimerRunning = false
                binding.btnStartPause.text = "Continue"
                binding.btnStartPause.setCompoundDrawablesWithIntrinsicBounds(
                    null,
                    ContextCompat.getDrawable(requireContext(), R.drawable.ic_start),
                    null,
                    null
                )
            }
            else {
                startTimer()
                isTimerRunning = true
                binding.btnStartPause.text = "Pause"
                binding.btnStartPause.setCompoundDrawablesWithIntrinsicBounds(
                    null,
                    ContextCompat.getDrawable(requireContext(), R.drawable.ic_pause),
                    null,
                    null
                )
            }

        }
    }

    private fun setupObservers() {
        lifecycleScope.launch {
            viewModel.loadWorkoutFlow.collectLatest { response ->
                when(response){
                    is Response.Error -> {
                        binding.progressBar.visibility = View.GONE
                    }
                    Response.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    is Response.Success -> {
                        viewModel.loadExercises(response.data.exercisesId)
                    }
                    else -> {
                        binding.progressBar.visibility = View.GONE
                    }
                }
            }
        }
        lifecycleScope.launch {
            viewModel.loadExercisesFlow.collectLatest { response ->
                when(response){
                    is Response.Error -> {
                        binding.progressBar.visibility = View.GONE
                    }
                    Response.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    is Response.Success -> {
                        binding.progressBar.visibility = View.GONE
                        viewModel.exercises = response.data
                        exerciseRepetions = response.data.map { it.repetitions }.toMutableList()
                        startWorkout()
                    }
                    else -> {
                        binding.progressBar.visibility = View.GONE
                    }
                }
            }
        }
    }

    private fun startWorkout() {
        currentTimerType = TimerType.TIMER_TYPE_REST
        val exercise = viewModel.exercises.getOrNull(currentExercise)
        if(exercise != null){
            startRest(exercise)
        }
        else{
            finishWorkout()
        }
    }

    private fun startTimer(time: Long = timeLeftInMillis) {
        isTimerRunning = true
        countDownTimer = object :CountDownTimer(time, 1000L){
            override fun onTick(millisUntilFinished: Long) {
                timeLeftInMillis = millisUntilFinished
                binding.tvTime.text = (millisUntilFinished / 1000).toInt().formatTime()
            }

            override fun onFinish() {
                val exercise = viewModel.exercises.getOrNull(currentExercise)
                when(currentTimerType){
                    TimerType.TIMER_TYPE_EXERCISE -> {
                        if(exercise == null){
                            finishWorkout()
                        }
                        else{
                            startRest(exercise)
                        }
                    }
                    TimerType.TIMER_TYPE_REST -> {
                        currentTimerType = TimerType.TIMER_TYPE_EXERCISE
                        if(exercise == null){
                            finishWorkout()
                        }
                        else{
                            startExercise(exercise)
                        }
                    }
                }
            }

        }
        countDownTimer.start()
    }

    private fun startExercise(exercise: Exercise) {
        exerciseRepetions[currentExercise] = exerciseRepetions[currentExercise] - 1
        binding.ivExercise.visibility = View.VISIBLE
        binding.tvExerciseCount.visibility = View.VISIBLE
        binding.ivNextExercise.visibility = View.GONE
        binding.tvGetReady.visibility = View.GONE

        binding.tvExerciseCount.text = "Exercises: ${currentExercise+1} / ${viewModel.exercises.size}"
        binding.tvExerciseTitle.text = "${exercise.title} ${exercise.repetitions - exerciseRepetions[currentExercise]} / ${exercise.repetitions}"
        Glide.with(requireContext()).load(exercise.imageUrl)
            .into(binding.ivExercise)
        startTimer(exercise.duration * 1000L)
        if(exerciseRepetions[currentExercise] <= 0){
            currentExercise++
        }

    }

    private fun startRest(exercise: Exercise) {
        binding.ivNextExercise.visibility = View.VISIBLE
        binding.tvGetReady.visibility = View.VISIBLE
        binding.ivExercise.visibility = View.GONE
        binding.tvExerciseCount.visibility = View.GONE

        Glide.with(requireContext()).load(exercise.imageUrl)
            .into(binding.ivNextExercise)
        binding.tvExerciseTitle.text = "Next: ${exercise.title}"
        currentTimerType = TimerType.TIMER_TYPE_REST
        startTimer(restTimeInSeconds * 1000L)
    }

    private fun finishWorkout() {
        findNavController().navigate(TrainingFragmentDirections.actionTrainingFragmentToEndWorkoutFragment())
    }

}