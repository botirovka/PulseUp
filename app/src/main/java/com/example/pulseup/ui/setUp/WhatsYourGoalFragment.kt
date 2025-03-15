package com.example.pulseup.ui.setUp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.pulseup.R
import com.example.pulseup.databinding.FragmentWhatsYourGoalBinding

class WhatsYourGoalFragment : Fragment() {
private lateinit var binding: FragmentWhatsYourGoalBinding

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
        binding.btnLoseWeight.isChecked = true
    }

    private fun setupListeners() {
        binding.btnContinue.setOnClickListener {
            findNavController().navigate(WhatsYourGoalFragmentDirections.actionWhatsYourGoalFragmentToPhysicalLevelFragment())
        }
    }

}