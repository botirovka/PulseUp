package com.example.pulseup.ui.setUp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.pulseup.databinding.FragmentHeightBinding
import kotlin.math.sqrt


class HeightFragment : Fragment() {
   private lateinit var binding: FragmentHeightBinding
    private lateinit var snapHelper: LinearSnapHelper
    private lateinit var adapter: WeightHeightAdapter
    private val args: HeightFragmentArgs by navArgs()
    private val heightList = (100..250).toList()

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
                        val selectedHeight = heightList[position]
                        adapter.setSelectedItem(position)
                        binding.tvHeight.text = "$selectedHeight"
                    }
                }
            }
        })
        binding.btnContinue.setOnClickListener {
            findNavController().navigate(HeightFragmentDirections.actionHeightFragmentToWhatsYourGoalFragment())
        }
    }

    private fun setupUI() {
        adapter = WeightHeightAdapter(heightList, WeightHeightAdapter.TYPE_HEIGHT) { selectedHeightPos ->
            val selectedHeight = heightList[selectedHeightPos]
            binding.rvHeightSelector.smoothScrollToPosition(selectedHeightPos)
            binding.tvHeight.text = "$selectedHeight"
        }
        binding.rvHeightSelector.adapter = adapter
        snapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(binding.rvHeightSelector)
        binding.rvHeightSelector.post {
            val height = estimateHeight(args.weight)
            binding.rvHeightSelector.scrollToPosition(height - 105)
            binding.rvHeightSelector.smoothScrollToPosition(height-100)
        }

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