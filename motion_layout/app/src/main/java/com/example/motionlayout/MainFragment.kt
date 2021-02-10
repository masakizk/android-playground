package com.example.motionlayout

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.motionlayout.databinding.FragmentMainBinding

class MainFragment : Fragment() {
    private lateinit var binding: FragmentMainBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainBinding.inflate(inflater, container, false).apply {
            constraintLayout.setOnClickListener { findNavController().navigate(R.id.action_mainFragment_to_constraintFragment) }
            constraintTransition.setOnClickListener { findNavController().navigate(R.id.action_mainFragment_to_transitionFragment) }
            constraintPropertySet.setOnClickListener { findNavController().navigate(R.id.action_mainFragment_to_propertySetFragment) }
            constraintMotion.setOnClickListener { findNavController().navigate(R.id.action_mainFragment_to_motionFragment) }
            constraintCustomAttribute.setOnClickListener { findNavController().navigate(R.id.action_mainFragment_to_customAttributeFragment) }
            transitionMotionInterpolator.setOnClickListener { findNavController().navigate(R.id.action_mainFragment_to_motionInterpolator) }
            transitionOnClick.setOnClickListener { findNavController().navigate(R.id.action_mainFragment_to_onClickFragment) }
        }
        return binding.root
    }
}