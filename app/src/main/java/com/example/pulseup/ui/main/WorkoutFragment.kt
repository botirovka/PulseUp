package com.example.pulseup.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.domain.models.Response
import com.example.domain.models.Workout
import com.example.pulseup.databinding.FragmentWorkoutBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class WorkoutFragment : Fragment() {
    private lateinit var binding: FragmentWorkoutBinding
    private val viewModel: WorkoutViewModel by viewModels()
    private val args : WorkoutFragmentArgs by navArgs()
    private lateinit var adapter: ExerciseAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentWorkoutBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        viewModel.loadWorkout(args.workoutId)
        setupObservers()
        setupListeners()
    }

    private fun setupListeners() {
        binding.btnStartWorkout.setOnClickListener {
            findNavController().navigate(WorkoutFragmentDirections.actionWorkoutFragmentToTrainingFragment(args.workoutId))
        }
    }

    private fun setupUI() {
        adapter = ExerciseAdapter()
        binding.rvExercisesList.adapter = adapter
    }

    private fun setupObservers() {
        lifecycleScope.launch {
            viewModel.loadWorkoutFlow.collectLatest { response ->
                when(response){
                    is Response.Error -> {}
                    Response.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    is Response.Success -> {
                        setupWorkoutDescriptionUI(response.data)
                        viewModel.loadExercises(response.data.exercisesId)
                    }
                    else -> {}
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
                        adapter.submitList(response.data)
                    }
                    else -> {
                        binding.progressBar.visibility = View.GONE
                    }
                }
            }
        }
    }

    private fun setupWorkoutDescriptionUI(workout: Workout) {
        binding.tvWorkoutTime.text = "${workout.duration} Minutes"
        binding.tvWorkoutCaloriesCount.text = "${workout.calories} Kcal"
        //to do
        binding.tvWorkoutLevel.text  = "Beginner"
        if (workout.imageUrl.isNotBlank()){
            Glide.with(binding.ivWorkoutImage.context).load(workout.imageUrl)
                .into(binding.ivWorkoutImage)
        }

    }


}