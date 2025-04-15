package com.example.pulseup.ui.main.workout

import android.media.MediaPlayer
import android.media.SoundPool
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.domain.models.Levels
import com.example.pulseup.R
import com.example.pulseup.databinding.FragmentEndWorkoutBinding
import com.example.pulseup.formatTime
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class EndWorkoutFragment : Fragment() {
    private val viewModel: WorkoutViewModel by activityViewModels()
    private lateinit var binding: FragmentEndWorkoutBinding
    private var calories = 0
    private var duration = 0
    private val soundPool = SoundPool.Builder()
        .setMaxStreams(1)
        .build()
    private var soundId: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEndWorkoutBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.loadUserData()
        setupUI()
        setupListeners()
        setupObservers()
        soundId = soundPool.load(requireContext(), R.raw.achievement, 1)
    }

    private fun setupObservers() {
        lifecycleScope.launch {
            viewModel.loadUserInfoFlow.collect { response ->
                Log.d("mydebug", "setupObservers Profile: $response")
                if (response.isSuccess) {
                    val user = response.getOrNull()
                    user?.let {
                        val history = it.historyWorkouts.plus(viewModel.workoutId).let { list ->
                            if (list.size > 3) list.drop(1) else list
                        }
                       viewModel.uploadUserData(
                           user.copy(
                               historyWorkouts = history,
                               totalWorkoutCount = it.totalWorkoutCount + 1,
                               totalWorkoutDuration = it.totalWorkoutDuration + (duration / 60),
                               totalWorkoutCalories = it.totalWorkoutCalories + calories
                           )
                       )
                    }
                }
            }
        }
    }


    private fun setupListeners() {
        binding.btnFinishWorkout.setOnClickListener {
            soundPool.play(soundId, 1f, 1f, 1, 0, 1f)
            findNavController().popBackStack()
        }
        binding.toolbar.goBackImageView.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun setupUI() {
        calories = viewModel.exercises.sumOf { it.calories }
        duration = viewModel.exercises.sumOf { it.duration * it.repetitions}
        binding.tvCalories.text = "$calories Kcal"
        binding.tvDuration.text = "${duration.formatTime()} min"
    }
}