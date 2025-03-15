package com.example.pulseup.ui.auth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.pulseup.databinding.FragmentStartBinding
import com.example.pulseup.ui.auth.Constants.LOG_IN_SCREEN_TYPE
import com.example.pulseup.ui.auth.Constants.SIGN_UP_SCREEN_TYPE


class StartFragment : Fragment() {
    private lateinit var binding: FragmentStartBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStartBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupListeners()

    }

    private fun setupListeners() {
        binding.btnSignUp.setOnClickListener {
            findNavController().navigate(StartFragmentDirections.actionStartFragmentToSignUpChooseFragment(SIGN_UP_SCREEN_TYPE))
        }
        binding.btnLogIn.setOnClickListener {
            findNavController().navigate(StartFragmentDirections.actionStartFragmentToSignUpChooseFragment(LOG_IN_SCREEN_TYPE))
        }
    }


}