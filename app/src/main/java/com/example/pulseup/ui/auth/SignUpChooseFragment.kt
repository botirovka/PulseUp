package com.example.pulseup.ui.auth

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.credentials.CredentialManager
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.domain.models.Response
import com.example.pulseup.databinding.FragmentSignUpChooseBinding
import com.example.pulseup.getGoogleCredentials
import com.example.pulseup.ui.auth.Constants.LOG_IN_SCREEN_TYPE
import com.example.pulseup.ui.auth.Constants.SIGN_UP_SCREEN_TYPE
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SignUpChooseFragment : Fragment() {
    private lateinit var binding: FragmentSignUpChooseBinding
    private val args: SignUpChooseFragmentArgs by navArgs()
    private val viewModel: SignUpChooseViewModel by viewModels()

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
        setupUI()
        setupListeners()
        setupObservers()
    }

    private fun setupListeners() {
        binding.toolbar.goBackImageView.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.btnAuthEmail.setOnClickListener {
            findNavController().navigate(SignUpChooseFragmentDirections.actionSignUpChooseFragmentToAuthEmailFragment(args.screenType))
        }
        binding.btnAuthGoogle.setOnClickListener {
            authWithGoogle()
        }
    }

    private fun setupObservers() {
        lifecycleScope.launch {
            viewModel.authFlow.collect { response ->
                setupUI()
                when(response){
                    is Response.Loading -> {binding.progressBar.visibility = View.VISIBLE}
                    is Response.Error -> {
                        Toast.makeText(requireContext(),response.message,Toast.LENGTH_SHORT).show()
                    }
                    is Response.Success -> {
                        when(args.screenType){
                            SIGN_UP_SCREEN_TYPE ->  Toast.makeText(requireContext(),"Success SignUp",Toast.LENGTH_SHORT).show()
                            LOG_IN_SCREEN_TYPE ->  Toast.makeText(requireContext(),"Success LogIn",Toast.LENGTH_SHORT).show()
                            else -> throw Exception("INVALID SCREEN TYPE")
                        }
                    }
                    else -> Toast.makeText(requireContext(),"Oops, something went wrong.",Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setupUI() {
        binding.progressBar.visibility = View.GONE
    }

    private fun authWithGoogle() {
        lifecycleScope.launch {
            val googleCredentials = try {
                getGoogleCredentials(CredentialManager.create(requireContext()))
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
            googleCredentials?.let {
                viewModel.authWithGoogle(it)
            }
        }
    }
}