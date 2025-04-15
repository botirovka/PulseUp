package com.example.pulseup.ui.main.profile

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.pulseup.databinding.FragmentProfileBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding
    private val viewModel: ProfileViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(layoutInflater)
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
            viewModel.loadUserInfoFlow.collect { response ->
                Log.d("mydebug", "setupObservers Profile: $response")
                if (response.isSuccess) {
                    val user = response.getOrNull()
                    user?.let {
                        withContext(Dispatchers.Main){
                            binding.tvPersonFullName.text = user.fullName
                            binding.tvPersonEmail.text = user.email
                            binding.tvHeight.text = user.height.toString()
                            binding.tvWeight.text = user.weight.toString()
                            binding.tvYears.text = user.age.toString()
                        }
                    }
                }
            }
        }
    }

    private fun setupUI() {
        viewModel.loadUserData()
    }

    private fun setupListeners() {
        binding.viewLogout.setOnClickListener {
            val bottomSheetDialog = LogOutFragment()
            bottomSheetDialog.show(childFragmentManager, LogOutFragment::class.java.simpleName)
        }
        binding.viewEditProfile.setOnClickListener {
            val bottomSheetDialog = EditProfileFragment()
            bottomSheetDialog.show(childFragmentManager, EditProfileFragment::class.java.simpleName)
        }
    }
}