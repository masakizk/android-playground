package com.example.daggerhilt.viewmodel

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.daggerhilt.calculator.Calculator

class MainActivityViewModel @ViewModelInject constructor(
        @Assisted private val savedStateHandle: SavedStateHandle,
        private val calculator: Calculator
) : ViewModel() {
    fun add(a: Int, b: Int) = calculator.add(a, b)
}