package com.example.motionlayout.constraint

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.motionlayout.databinding.FragmentConstraintCustomAttributeBinding

class CustomAttributeFragment: Fragment() {
    private lateinit var binding: FragmentConstraintCustomAttributeBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentConstraintCustomAttributeBinding.inflate(inflater, container, false)
        return binding.root
    }
}