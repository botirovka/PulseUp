package com.example.pulseup.ui.auth

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.domain.models.Response
import com.example.pulseup.databinding.FragmentAuthEmailBinding
import com.example.pulseup.ui.LoadingAnimation
import com.example.pulseup.ui.auth.Constants.LOG_IN_SCREEN_TYPE
import com.example.pulseup.ui.auth.Constants.SIGN_UP_SCREEN_TYPE
import com.example.pulseup.validateEmail
import com.example.pulseup.validatePassword
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AuthEmailFragment : Fragment() {
    private lateinit var binding: FragmentAuthEmailBinding
    private val args : AuthEmailFragmentArgs by navArgs()
    private val viewModel: AuthEmailViewModel by viewModels()
    private lateinit var loadingAnimation: LoadingAnimation
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAuthEmailBinding.inflate(layoutInflater)
        loadingAnimation = LoadingAnimation(binding.btnAuthEmail)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUi()
        setupListeners()
        setupTextWatchers()
        setupObservers()
    }

    private fun setupObservers() {

        lifecycleScope.launch {
            viewModel.authFlow.collect { response ->
                loadingAnimation.stop()
                setupUi()
                when(response){
                    is Response.Loading -> loadingAnimation.start()
                    is Response.Error -> {
                        binding.passwordTextInputLayout.error = response.message
                    }
                    is Response.Success -> {
                        when(args.screenType){
                            SIGN_UP_SCREEN_TYPE -> {}
                            LOG_IN_SCREEN_TYPE -> {}
                            else -> throw Exception("INVALID SCREEN TYPE")
                        }
                    }

                    Response.EmailAlreadyInUse -> {
                        binding.passwordTextInputLayout.error = "This email address already in use"
                    }
                    Response.EmailNotFound -> {
                        binding.passwordTextInputLayout.error = "This email not found"
                    }
                    Response.LinkToGoogle -> {
                        //here will be google link
                        binding.passwordTextInputLayout.error = "Google link"
                    }
                    Response.WrongPassword -> {
                        binding.passwordTextInputLayout.error = "The email address or password is incorrect"
                    }
                }
            }
        }
    }

    private fun setupUi() {
        when(args.screenType){
            SIGN_UP_SCREEN_TYPE -> {
                binding.btnAuthEmail.text = "Sign Up"
                binding.groupForgotPassword.visibility = View.GONE
            }
            LOG_IN_SCREEN_TYPE -> {
                binding.btnAuthEmail.text = "Log In"
                binding.groupForgotPassword.visibility = View.VISIBLE
            }
            else -> throw Exception("INVALID SCREEN TYPE")
        }
    }

    private fun setupListeners() {
        binding.toolbar.goBackImageView.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.tvResetPassword.setOnClickListener {
            Toast.makeText(requireContext(),"Not Implemented yet(", Toast.LENGTH_SHORT).show()
        }
        binding.btnAuthEmail.setOnClickListener {
            if(validateEmail()){
                when(args.screenType){
                    SIGN_UP_SCREEN_TYPE -> signUp()
                    LOG_IN_SCREEN_TYPE -> login()
                    else -> throw Exception("INVALID SCREEN TYPE")
                }
            }
        }
        binding.passwordEditText.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                val isEmailValid = validateEmail()
                val isPasswordValid = validatePassword()
                if(isEmailValid and isPasswordValid){
                    when(args.screenType){
                        SIGN_UP_SCREEN_TYPE -> signUp()
                        LOG_IN_SCREEN_TYPE -> login()
                        else -> throw Exception("INVALID SCREEN TYPE")
                    }
                    hideKeyboard(binding.passwordTextInputLayout)
                }
                if (isEmailValid.not()){
                    binding.emailEditText.requestFocus()
                }
                true
            } else {
                false
            }
        }
    }

    private fun signUp() {
        viewModel.register(
            binding.emailEditText.text.toString().lowercase(),
            binding.passwordEditText.text.toString()
        )
    }

    private fun login() {
        viewModel.login(
            binding.emailEditText.text.toString().lowercase(),
            binding.passwordEditText.text.toString()
        )
    }

    private fun validateEmail(): Boolean {
        val isEmailValid = binding.emailEditText.text.toString().validateEmail()
        if(isEmailValid.not()){
            binding.emailTextInputLayout.error = "Email not valid"
        }
        else{
            binding.emailTextInputLayout.error = null
        }
        return isEmailValid

    }

    private fun validatePassword(): Boolean {
        val isPasswordValid = binding.passwordEditText.text.toString().validatePassword()
        if(isPasswordValid.not()){
            binding.passwordTextInputLayout.error = "Password must be at least 8 characters"
        }
        else{
            binding.passwordTextInputLayout.error = null
        }
        return isPasswordValid
    }

    private fun setupTextWatchers() {
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                updateInputLayoutState()
            }
        }
        binding.passwordEditText.addTextChangedListener(textWatcher)
        binding.emailEditText.addTextChangedListener(textWatcher)
    }

    private fun updateInputLayoutState() {
        val isEmailValid = binding.emailEditText.text.toString().validateEmail()
        if(isEmailValid){
            binding.emailTextInputLayout.error = null
        }
        val isPasswordValid = binding.passwordEditText.text.toString().validatePassword()
        if(isPasswordValid){
            binding.passwordTextInputLayout.error = null
        }
    }

    private fun hideKeyboard(view: View) {
        val inputMethodManager =
            view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        inputMethodManager?.hideSoftInputFromWindow(view.windowToken, 0)
    }

}