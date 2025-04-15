package com.example.pulseup.ui.main.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.domain.models.Levels
import com.example.domain.models.Response
import com.example.domain.models.User
import com.example.pulseup.NavGraphDirections
import com.example.pulseup.R
import com.example.pulseup.databinding.FragmentEditProfileBinding
import com.example.pulseup.databinding.FragmentLogOutBinding
import com.example.pulseup.ui.main.MainFragment
import com.example.pulseup.ui.main.MainFragmentDirections
import com.example.pulseup.ui.setUp.FillYourProfileFragmentDirections
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class EditProfileFragment : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentEditProfileBinding
    private val viewModel: ProfileViewModel by activityViewModels()
    private var selectedLevel: String? = null
    val levelItems: List<String> = Levels.entries
        .filterNot { it == Levels.NONE }
        .map { it.name }

    private lateinit var adapterItems: ArrayAdapter<String>
    override fun onStart() {
        super.onStart()
        val dialog = dialog as? BottomSheetDialog
        val bottomSheet = dialog?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
        bottomSheet?.let {
            val behavior = BottomSheetBehavior.from(it)
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
            behavior.skipCollapsed = true
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEditProfileBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapterItems = ArrayAdapter<String>(requireContext(), R.layout.item_levels_list,levelItems)
        binding.autoCompleteTV.setAdapter(adapterItems)
        viewModel.loadUserData()
        setupListeners()
        setupObservers()
    }

    private fun setupObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.loadUserInfoFlow.collect { response ->
                        if (response.isSuccess) {
                            val user = response.getOrNull()
                            user?.let {
                                viewModel.user = user
                                binding.nameEditText.setText(it.fullName)
                                binding.nickNameEditText.setText(it.nickName)
                                binding.weightEditText.setText(it.weight.toString())
                                binding.heightEditText.setText(it.height.toString())
                                binding.autoCompleteTV.setText(it.level.name, false)
                            }
                        }
                    }
                }
            }
        }
        lifecycleScope.launch {
            viewModel.uploadUserInfoFlow.collect { response ->
                when(response){
                    is Response.Error -> {
                        Toast.makeText(requireContext(),"ERROR: ${response.message}", Toast.LENGTH_SHORT).show()
                    }
                    is Response.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                        binding.groupInputFields.visibility = View.INVISIBLE
                    }
                    is Response.Success -> {
                        viewModel.loadUserData()
                        dialog?.dismiss()
                    }
                    else -> {}
                }
            }
        }
    }

    private fun setupListeners() {
        binding.btnUpdateProfile.setOnClickListener {
            if(isInputValid()){
                viewModel.uploadUserData(
                    viewModel.user.copy(
                        fullName = binding.nameEditText.text.toString(),
                        nickName = binding.nickNameEditText.text.toString(),
                        weight =  binding.weightEditText.text.toString().toIntOrNull() ?: 50,
                        height =  binding.heightEditText.text.toString().toIntOrNull() ?: 150,
                        level = Levels.valueOf(binding.autoCompleteTV.text.toString())
                    )

                )
            }
        }
        binding.autoCompleteTV.setOnItemClickListener{ parent, _, position, _ ->
            selectedLevel = parent.getItemAtPosition(position) as String
        }
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

        val isWeightValid = binding.weightEditText.text.toString().isNotBlank()
        if(isWeightValid.not()){
            binding.weightTextInputLayout.error = "* required field"
        }
        else binding.weightTextInputLayout.error = null

        val isHeightValid = binding.heightEditText.text.toString().isNotBlank()
        if(isHeightValid.not()){
            binding.heightTextInputLayout.error = "* required field"
        }
        else binding.heightTextInputLayout.error = null
        return isNickNameValid and isNickNameValid and isWeightValid and isHeightValid
    }

}