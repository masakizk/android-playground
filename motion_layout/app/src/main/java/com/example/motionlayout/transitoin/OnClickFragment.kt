package com.example.motionlayout.transitoin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.motionlayout.databinding.FragmentTransitionOnClickBinding

class OnClickFragment : Fragment() {
    private lateinit var binding: FragmentTransitionOnClickBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTransitionOnClickBinding.inflate(inflater, container, false)
        return binding.root
    }
}