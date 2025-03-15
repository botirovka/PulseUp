package com.example.pulseup.ui.auth

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.pulseup.databinding.FragmentSignUpChooseBinding
import com.example.pulseup.ui.auth.Constants.LOG_IN_SCREEN_TYPE
import com.example.pulseup.ui.auth.Constants.SIGN_UP_SCREEN_TYPE

class SignUpChooseFragment : Fragment() {
    private lateinit var binding: FragmentSignUpChooseBinding
    private val args: SignUpChooseFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSignUpChooseBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("debug", "onViewCreated: ${args.screenType}")
        when(args.screenType){
            SIGN_UP_SCREEN_TYPE -> binding.tvHeadingWelcome.text = "Sign Up"
            LOG_IN_SCREEN_TYPE -> binding.tvHeadingWelcome.text = "Log In"
            else -> throw Exception("INVALID SCREEN TYPE")
        }
        setupListeners()
    }

    private fun setupListeners() {
        binding.toolbar.goBackImageView.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.btnAuthEmail.setOnClickListener {
            findNavController().navigate(SignUpChooseFragmentDirections.actionSignUpChooseFragmentToAuthEmailFragment(args.screenType))
        }
    }


}