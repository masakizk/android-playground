package com.example.mediastore.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.mediastore.R
import com.example.mediastore.databinding.FragmentRootBinding

class RootFragment : Fragment() {
    private lateinit var binding: FragmentRootBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRootBinding.inflate(inflater, container, false)
        binding.apply {
            buttonShowImages.setOnClickListener { findNavController().navigate(R.id.action_root_to_image_view) }
            buttonTakePhoto.setOnClickListener { findNavController().navigate(R.id.action_root_to_take_photo) }
        }
        return binding.root
    }
}