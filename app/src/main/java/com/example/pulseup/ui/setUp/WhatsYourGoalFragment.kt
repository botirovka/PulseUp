package com.example.pulseup.ui.setUp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.domain.models.Goals
import com.example.pulseup.R
import com.example.pulseup.databinding.FragmentWhatsYourGoalBinding

class WhatsYourGoalFragment : Fragment() {
private lateinit var binding: FragmentWhatsYourGoalBinding
private val viewModel: SetUpViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentWhatsYourGoalBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        setupListeners()
    }

    private fun setupUI() {
        when(viewModel.userState.value.goal){
            Goals.LOSE_WEIGHT -> binding.btnLoseWeight.isChecked = true
            Goals.GAIN_WEIGHT -> binding.btnGainWeight.isChecked = true
            Goals.MUSCLE_MASS_GAIN -> binding.btnMuscleGain.isChecked = true
            Goals.SHAPE_BODY -> binding.btnShapeBody.isChecked = true
            Goals.OTHERS -> binding.btnOthers.isChecked = true
            Goals.NONE -> binding.btnLoseWeight.isChecked = true
        }
    }

    private fun setupListeners() {
        binding.btnContinue.setOnClickListener {
            val selectedGoal: Goals = when (binding.rgGoalsButtons.checkedRadioButtonId) {
                R.id.btnLoseWeight-> Goals.LOSE_WEIGHT
                R.id.btnGainWeight -> Goals.GAIN_WEIGHT
                R.id.btnMuscleGain -> Goals.MUSCLE_MASS_GAIN
                R.id.btnShapeBody -> Goals.SHAPE_BODY
                R.id.btnOthers -> Goals.OTHERS
                else -> Goals.OTHERS
            }
            viewModel.setGoal(selectedGoal)
            findNavController().navigate(WhatsYourGoalFragmentDirections.actionWhatsYourGoalFragmentToPhysicalLevelFragment())
        }
    }

}