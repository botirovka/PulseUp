package com.example.pulseup.ui.main.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.pulseup.NavGraphDirections
import com.example.pulseup.R
import com.example.pulseup.databinding.FragmentLogOutBinding
import com.example.pulseup.ui.main.MainFragment
import com.example.pulseup.ui.main.MainFragmentDirections
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LogOutFragment : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentLogOutBinding
    private val viewModel: ProfileViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLogOutBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupListeners()
    }

    private fun setupListeners() {
        binding.btnLogOut.setOnClickListener {
            viewModel.logOut()
            findNavController().navigate(MainFragmentDirections.actionMainFragmentToStartFragment())
            this.dialog?.dismiss()
        }
        binding.btnCancel.setOnClickListener {
            this.dialog?.dismiss()
        }
    }

}