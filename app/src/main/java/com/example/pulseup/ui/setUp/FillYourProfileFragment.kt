package com.example.pulseup.ui.setUp

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.example.pulseup.databinding.FragmentFillYourProfileBinding

class FillYourProfileFragment : Fragment() {
    private lateinit var binding: FragmentFillYourProfileBinding
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
        setupListeners()
    }

    private fun setupListeners() {
        binding.ivProfileImage.setOnClickListener {
            pickImage()
        }
    }

    private fun pickImage(){
        binding.ivProfileImage.animate()
            .scaleX(0.9f)
            .scaleY(0.9f)
            .start()
        pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

}