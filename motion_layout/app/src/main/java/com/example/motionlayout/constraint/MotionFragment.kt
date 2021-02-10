package com.example.motionlayout.constraint

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.motionlayout.databinding.FragmentConstraintMotionBinding
import com.example.motionlayout.databinding.FragmentMainBinding

class MotionFragment: Fragment() {
    lateinit var binding: FragmentConstraintMotionBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentConstraintMotionBinding.inflate(inflater, container, false)
        return binding.root
    }
}