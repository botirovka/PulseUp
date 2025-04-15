package com.example.pulseup.ui.setUp.weightHeight

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.pulseup.databinding.FragmentWhatsYourWeightBinding
import com.example.pulseup.ui.setUp.SetUpViewModel


class WhatsYourWeightFragment : Fragment() {
    private lateinit var binding: FragmentWhatsYourWeightBinding
    private lateinit var snapHelper: LinearSnapHelper
    private lateinit var adapter: WeightHeightAdapter
    private val viewModel: SetUpViewModel by activityViewModels()
    private var selectedWeight = 55
    private val weightList = (25..200).toList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentWhatsYourWeightBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        setupListeners()
        Log.d("mydebug", "scroll $selectedWeight")
        if(savedInstanceState != null){
            binding.rvWeightSelector.scrollToPosition(selectedWeight-25)
        }
        else{
            binding.rvWeightSelector.scrollToPosition(selectedWeight-20)
        }
        binding.rvWeightSelector.post {
            binding.rvWeightSelector.smoothScrollToPosition(selectedWeight-25)
        }
    }

    private fun setupListeners() {
        binding.rvWeightSelector.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    Log.d("mydebug", "here")
                    val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                    val snapView = snapHelper.findSnapView(layoutManager)
                    val position = layoutManager.getPosition(snapView!!)

                    if (position != RecyclerView.NO_POSITION) {
                        Log.d("mydebug", "here problem")
                        selectedWeight = weightList[position]
                        adapter.setSelectedItem(position)
                        binding.tvWeight.text = "$selectedWeight"
                    }
                }
            }
        })
        binding.btnContinue.setOnClickListener {
            viewModel.setWeight(selectedWeight)
            findNavController().navigate(WhatsYourWeightFragmentDirections.actionWhatsYourWeightFragmentToHeightFragment(selectedWeight))
        }
    }

    private fun setupUI() {
        if(viewModel.userState.value.weight > 0){
            selectedWeight = viewModel.userState.value.weight
        }
        adapter = WeightHeightAdapter(weightList, WeightHeightAdapter.TYPE_WEIGHT) { selectedWeightPos ->
            selectedWeight = weightList[selectedWeightPos]
            binding.rvWeightSelector.smoothScrollToPosition(selectedWeightPos)
            binding.tvWeight.text = "$selectedWeight"
        }
        binding.rvWeightSelector.adapter = adapter
        snapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(binding.rvWeightSelector)

    }


}