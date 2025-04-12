package com.example.pulseup.ui.setUp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.domain.models.Genders
import com.example.pulseup.R
import com.example.pulseup.databinding.FragmentGenderBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GenderFragment : Fragment() {
   private lateinit var binding: FragmentGenderBinding
   private val viewModel: SetUpViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentGenderBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        setupListeners()
    }

    private fun setupUI() {
        when(viewModel.userState.value.gender){
            Genders.MALE ->  binding.btnMaleGender.isChecked = true
            Genders.FEMALE ->  binding.btnFemaleGender.isChecked = true
            Genders.NONE -> {binding.btnMaleGender.isChecked = true}
        }
    }

    private fun setupListeners() {
        binding.btnContinue.setOnClickListener {
            val selectedGender: Genders = when (binding.rgGenderButtons.checkedRadioButtonId) {
                R.id.btnMaleGender -> Genders.MALE
                R.id.btnFemaleGender -> Genders.FEMALE
                else -> Genders.MALE
            }
            viewModel.setGender(selectedGender)
            findNavController().navigate(GenderFragmentDirections.actionGenderFragmentToHowOldAreYouFragment())
        }
        binding.toolbar.goBackImageView.setOnClickListener {
            //show dialog
            viewModel.logout()
            findNavController().popBackStack()
        }
    }
}