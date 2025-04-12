package com.example.pulseup.ui.setUp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.pulseup.databinding.FragmentHowOldAreYouBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HowOldAreYouFragment : Fragment() {
    private lateinit var binding: FragmentHowOldAreYouBinding
    private lateinit var snapHelper: LinearSnapHelper
    private lateinit var adapter: AgeAdapter
    private val viewModel: SetUpViewModel by activityViewModels()
    private val ageList = (18..60).toList()
    private var selectedAge = 22

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHowOldAreYouBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        setupListeners()
        if(savedInstanceState != null){
            binding.rvAgeSelector.scrollToPosition(selectedAge-18)
        }
        binding.rvAgeSelector.post {
            binding.rvAgeSelector.smoothScrollToPosition(selectedAge-18)
        }
    }

    private fun setupListeners() {
        binding.rvAgeSelector.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val snapView = snapHelper.findSnapView(layoutManager)
                    val position = layoutManager.getPosition(snapView!!)

                    if (position != RecyclerView.NO_POSITION) {
                        selectedAge = ageList[position]
                        adapter.setSelectedItem(position)
                        binding.tvAge.text = "$selectedAge"
                    }
                }
            }
        })
        binding.btnContinue.setOnClickListener {
            viewModel.setAge(selectedAge)
            findNavController().navigate(HowOldAreYouFragmentDirections.actionHowOldAreYouFragmentToWhatsYourWeightFragment())
        }
    }

    private fun setupUI() {
        if(viewModel.userState.value.age > 0){
            selectedAge = viewModel.userState.value.age
        }
        adapter = AgeAdapter(ageList) { selectedAgePos ->
            selectedAge = ageList[selectedAgePos]
            binding.rvAgeSelector.smoothScrollToPosition(selectedAgePos)
            binding.tvAge.text = "$selectedAge"
        }
        binding.rvAgeSelector.adapter = adapter
        snapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(binding.rvAgeSelector)

    }



}