package com.example.pulseup.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.pulseup.databinding.FragmentEndWorkoutBinding
import com.example.pulseup.formatTime
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EndWorkoutFragment : Fragment() {
    private val viewModel: WorkoutViewModel by activityViewModels()
    private lateinit var binding: FragmentEndWorkoutBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEndWorkoutBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        setupListeners()
    }

    private fun setupListeners() {
        binding.btnFinishWorkout.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.toolbar.goBackImageView.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun setupUI() {
        val calories = viewModel.exercises.sumOf { it.calories }
        val duration = viewModel.exercises.sumOf { it.duration * it.repetitions}
        binding.tvCalories.text = "$calories Kcal"
        binding.tvDuration.text = "${duration.formatTime()} min"
    }
}