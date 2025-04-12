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
import com.example.pulseup.databinding.FragmentHeightBinding
import kotlin.math.sqrt


class HeightFragment : Fragment() {
   private lateinit var binding: FragmentHeightBinding
    private lateinit var snapHelper: LinearSnapHelper
    private lateinit var adapter: WeightHeightAdapter
    private val viewModel: SetUpViewModel by activityViewModels()
    private val heightList = (100..250).toList()
    private var selectedHeight: Int = 160

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHeightBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        setupListeners()
        if(savedInstanceState != null){
            val selectedHeight = estimateHeight(viewModel.userState.value.weight)
            binding.rvHeightSelector.scrollToPosition(selectedHeight-100)
        }
        else{
            binding.rvHeightSelector.scrollToPosition(selectedHeight-100)
        }
        binding.rvHeightSelector.post {
            binding.rvHeightSelector.smoothScrollToPosition(selectedHeight-100)
        }
    }

    private fun setupListeners() {
        binding.rvHeightSelector.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val snapView = snapHelper.findSnapView(layoutManager)
                    val position = layoutManager.getPosition(snapView!!)

                    if (position != RecyclerView.NO_POSITION) {
                        selectedHeight = heightList[position]
                        adapter.setSelectedItem(position)
                        binding.tvHeight.text = "$selectedHeight"
                    }
                }
            }
        })
        binding.btnContinue.setOnClickListener {
            viewModel.setHeight(selectedHeight)
            findNavController().navigate(HeightFragmentDirections.actionHeightFragmentToWhatsYourGoalFragment())
        }
    }

    private fun setupUI() {
        if(viewModel.userState.value.height > 0){
            selectedHeight = viewModel.userState.value.height
        }
        adapter = WeightHeightAdapter(heightList, WeightHeightAdapter.TYPE_HEIGHT) { selectedHeightPos ->
                val selectedHeight = heightList[selectedHeightPos]
                binding.rvHeightSelector.smoothScrollToPosition(selectedHeightPos)
                binding.tvHeight.text = "$selectedHeight"
        }
        binding.rvHeightSelector.adapter = adapter
        snapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(binding.rvHeightSelector)
    }

    fun estimateHeight(weight: Int): Int {
        val bmi: Double = when {
            weight <= 40 -> 21.0
            weight <= 80 -> 22.0
            else -> weight / 3.6
        }
        return (sqrt(weight.toDouble() / bmi) * 100).toInt()
    }
}