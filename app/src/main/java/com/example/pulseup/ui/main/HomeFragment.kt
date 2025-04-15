package com.example.pulseup.ui.main

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.domain.models.Response
import com.example.domain.models.Workout
import com.example.pulseup.databinding.FragmentHomeBinding
import com.example.pulseup.ui.main.profile.ProfileViewModel
import com.example.pulseup.ui.main.workout.WorkoutAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private val viewModel: HomeViewModel by viewModels()
    private val profileViewModel: ProfileViewModel by activityViewModels()
    private lateinit var adapter: WorkoutAdapter
    private lateinit var adapterRecomendation: WorkoutAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        setupObservers()
        setupListeners()
        setupTextWatchers()
    }

    private fun setupListeners() {
        binding.toolbar.ivSearch.setOnClickListener {
            if(binding.toolbar.searchTextInputLayout.visibility == View.VISIBLE){
                binding.toolbar.searchTextInputLayout.visibility = View.GONE
            }
            else{
                binding.toolbar.searchTextInputLayout.visibility = View.VISIBLE
                binding.toolbar.searchEditText.text?.clear()
            }
        }
        binding.toolbar.searchTextInputLayout.setEndIconOnClickListener {
            binding.toolbar.searchEditText.text?.clear()
        }
        binding.rvRecomendationWorkoutList.addOnItemTouchListener(object: RecyclerView.OnItemTouchListener{
            override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                when(e.action){
                    MotionEvent.ACTION_DOWN -> {
                        binding.rvRecomendationWorkoutList.parent.requestDisallowInterceptTouchEvent(true)
                    }
                }
                return false
            }

            override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {
            }

            override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {
            }

        })
    }

    private fun setupTextWatchers() {
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val filteredWorkouts = viewModel.workouts.filter { it.title.lowercase().contains(s.toString().lowercase()) }

                adapter.submitList(filteredWorkouts)
                adapterRecomendation.submitList(filteredWorkouts)
            }
        }
        binding.toolbar.searchEditText.addTextChangedListener(textWatcher)
    }

    private fun setupObservers() {
        lifecycleScope.launch {
            profileViewModel.loadUserInfoFlow.collect { response ->
                Log.d("mydebug", "setupObservers HOME: $response")
                if (response.isSuccess) {
                    val user = response.getOrNull()
                    user?.let {
                        withContext(Dispatchers.Main){
                            binding.toolbar.tvToolbarHeading.text = "Hi, ${user.nickName}"
                        }
                    }
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.loadUserInfoFlow.collect { response ->
                        if (response.isSuccess) {
                            val username = response.getOrNull()?.nickName ?: "Friend"
                            binding.toolbar.tvToolbarHeading.text = "Hi, $username"
                        }
                    }
                }
                launch {
                    viewModel.loadWorkoutsFlow.collect { response ->
                        when (response) {
                            is Response.Loading -> {
                                Log.d("mydebug", "loading")
                                binding.progressBar.visibility = View.VISIBLE
                            }
                            is Response.Success -> {
                                binding.progressBar.visibility = View.GONE
                                binding.clContent.visibility = View.VISIBLE
                                Log.d("mydebug", "setupObservers: ${response.data}")
                                viewModel.workouts = response.data
                                val searchQuery = binding.toolbar.searchEditText.text.toString().lowercase()
                                val filteredWorkouts = viewModel.workouts.filter { it.title.lowercase().contains(searchQuery) }
                                adapter.submitList(filteredWorkouts)

                                adapterRecomendation.submitList(filteredWorkouts.reversed())
                            }
                            is Response.Error -> {
                                binding.progressBar.visibility = View.GONE
                            }
                            else -> {
                                binding.progressBar.visibility = View.GONE
                            }
                        }
                    }
                }
            }
        }
    }

    private fun setupUI() {
        adapter = WorkoutAdapter(::onItemClick, WorkoutAdapter.TYPE_FULL_WIDTH)
        binding.rvWorkoutList.adapter = adapter
        adapterRecomendation = WorkoutAdapter(::onItemClick, WorkoutAdapter.TYPE_SQUARE)
        binding.rvRecomendationWorkoutList.adapter = adapterRecomendation
        binding.progressBar.visibility = View.VISIBLE
        viewModel.loadUserData()
        viewModel.loadWorkouts()

    }

    private fun onItemClick(workout: Workout) {
        findNavController().navigate(MainFragmentDirections.actionMainFragmentToWorkoutFragment(workout.id))
    }

}