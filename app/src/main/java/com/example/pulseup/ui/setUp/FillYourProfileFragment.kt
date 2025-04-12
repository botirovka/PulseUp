package com.example.pulseup.ui.setUp

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.domain.models.Response
import com.example.pulseup.databinding.FragmentFillYourProfileBinding
import kotlinx.coroutines.launch

class FillYourProfileFragment : Fragment() {
    private lateinit var binding: FragmentFillYourProfileBinding
    private val viewModel: SetUpViewModel by activityViewModels()
    private val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        binding.ivProfileImage.animate()
            .scaleX(1f)
            .scaleY(1f)
            .start()
        if (uri != null) {
            binding.ivProfileImage.setImageURI(uri)
        } else {
            Log.d("PhotoPicker", "No media selected")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFillYourProfileBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
        setupListeners()
        setupObservers()
    }

    private fun setupObservers() {
        lifecycleScope.launch {
            viewModel.uploadUserInfoFlow.collect { response ->
                when(response){
                    is Response.Error -> {
                        Toast.makeText(requireContext(),"ERROR: ${response.message}",Toast.LENGTH_SHORT).show()
                    }
                    is Response.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }
                    is Response.Success -> {
                        findNavController().navigate(FillYourProfileFragmentDirections.actionFillYourProfileFragmentToHomeFragment())
                    }
                    else -> {}
                }
            }
        }
    }

    private fun setupUI() {
        val fullName = viewModel.userState.value.fullName
        if(fullName.isNotBlank()){
            binding.nameEditText.setText(fullName)
        }
        val nickName = viewModel.userState.value.nickName
        if(nickName.isNotBlank()){
            binding.nickNameEditText.setText(nickName)
        }
    }

    private fun setupListeners() {
        binding.ivProfileImage.setOnClickListener {
            pickImage()
        }
        binding.btnStart.setOnClickListener {
            if(isInputValid()){
                viewModel.setFullName(binding.nameEditText.text.toString())
                viewModel.setNickName(binding.nickNameEditText.text.toString())
                viewModel.uploadUserData()
            }
        }
    }

    private fun pickImage(){
        binding.ivProfileImage.animate()
            .scaleX(0.9f)
            .scaleY(0.9f)
            .start()
        pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private fun isInputValid(): Boolean {
        val isFullNameValid = binding.nameEditText.text.toString().isNotBlank()
        if(isFullNameValid.not()){
            binding.nameTextInputLayout.error = "* required field"
        }
        else binding.nameTextInputLayout.error = null

        val isNickNameValid = binding.nickNameEditText.text.toString().isNotBlank()
        if(isNickNameValid.not()){
            binding.nickNameTextInputLayout.error = "* required field"
        }
        else binding.nameTextInputLayout.error = null
        return isNickNameValid and isNickNameValid
    }

}