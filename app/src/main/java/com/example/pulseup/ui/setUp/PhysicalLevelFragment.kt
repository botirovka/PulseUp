package com.example.pulseup.ui.setUp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.domain.models.Levels
import com.example.pulseup.R
import com.example.pulseup.databinding.FragmentPhysicalLevelBinding

class PhysicalLevelFragment : Fragment() {
    private lateinit var binding: FragmentPhysicalLevelBinding
    private val viewModel: SetUpViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPhysicalLevelBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        setupListeners()
    }

    private fun setupUI() {
        when(viewModel.userState.value.level){
            Levels.Beginner -> binding.btnBeginner.isChecked = true
            Levels.Intermediate -> binding.btnIntermediate.isChecked = true
            Levels.Advance -> binding.btnAdvance.isChecked = true
            Levels.NONE -> binding.btnBeginner.isChecked = true
        }
    }

    private fun setupListeners() {
        binding.btnContinue.setOnClickListener {
            val selectedLevel: Levels = when(binding.rgPhysicalLevelButtons.checkedRadioButtonId){
                R.id.btnBeginner -> Levels.Beginner
                R.id.btnIntermediate -> Levels.Intermediate
                R.id.btnAdvance -> Levels.Advance
                else -> Levels.Advance
            }
            viewModel.setLevel(selectedLevel)
            findNavController().navigate(PhysicalLevelFragmentDirections.actionPhysicalLevelFragmentToFillYourProfileFragment())
        }
    }

}