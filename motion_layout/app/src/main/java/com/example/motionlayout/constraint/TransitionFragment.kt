package com.example.motionlayout.constraint

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.motionlayout.databinding.FragmentConstraintTransitionBinding

class TransitionFragment : Fragment() {
    private lateinit var binding: FragmentConstraintTransitionBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentConstraintTransitionBinding.inflate(inflater, container, false)
        return binding.root
    }
}