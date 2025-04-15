package com.example.pulseup.ui.main.stats

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.domain.models.Response
import com.example.domain.models.Workout
import com.example.pulseup.databinding.FragmentStatsBinding
import com.example.pulseup.ui.main.HomeViewModel
import com.example.pulseup.ui.main.MainFragmentDirections
import com.example.pulseup.ui.main.workout.WorkoutAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class StatsFragment : Fragment() {
    private val viewModel: HomeViewModel by viewModels()
    private lateinit var binding: FragmentStatsBinding
    private lateinit var adapter: WorkoutAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStatsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        binding.rvWorkoutList.visibility = View.INVISIBLE
        adapter.submitList(emptyList())
        viewModel.loadUserData()
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        setupObservers()
    }

    private fun setupUI() {
        adapter = WorkoutAdapter(::onItemClick, WorkoutAdapter.TYPE_FULL_WIDTH)
        binding.rvWorkoutList.adapter = adapter
        binding.progressBarStats.visibility = View.VISIBLE
        binding.progressBarHistory.visibility = View.VISIBLE
        binding.groupStats.visibility = View.INVISIBLE
        binding.rvWorkoutList.visibility = View.INVISIBLE
    }

    private fun onItemClick(workout: Workout) {
        Log.d("mydebug", "onItemClick: ${workout.id}")
        findNavController().navigate(MainFragmentDirections.actionMainFragmentToWorkoutFragment(workout.id))
    }


    private fun setupObservers() {
        lifecycleScope.launch {
            viewModel.loadUserInfoFlow.collect { response ->
                if (response.isSuccess) {
                    binding.progressBarStats.visibility = View.GONE
                    val user = response.getOrNull()
                    user?.let {
                        viewModel.loadWorkouts(user.historyWorkouts)
                        binding.groupStats.visibility = View.VISIBLE
                        withContext(Dispatchers.Main) {
                            binding.tvLevel.text = "Level: ${user.level.name}"
                            binding.tvWorkoutCount.text =
                                "Total trainings: ${user.totalWorkoutCount}"
                            binding.tvCaloriesCount.text =
                                "Calories: ${user.totalWorkoutCalories} Kcal"
                            binding.tvDuration.text = "Total Time: ${user.totalWorkoutDuration} min"
                        }
                    }
                }
            }
        }
        lifecycleScope.launch {
            viewModel.loadWorkoutFlow.collect { response ->
                when (response) {
                    is Response.Loading -> {
                        Log.d("mydebug", "loading")
                        binding.progressBarHistory.visibility = View.VISIBLE
                    }

                    is Response.Success -> {
                        Log.d("mydebug", "setupObservers: ${response.data}")
                        binding.progressBarHistory.visibility = View.GONE
                        binding.rvWorkoutList.visibility = View.VISIBLE
                        adapter.addItemToList(response.data)
                    }

                    is Response.Error -> {
                        binding.progressBarHistory.visibility = View.GONE
                    }

                    else -> {
                        binding.progressBarHistory.visibility = View.GONE
                    }
                }
            }
        }
    }
}

