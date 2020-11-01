package com.example.daggerhilt.viewmodel

import android.content.Context
import android.widget.Toast
import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel

class MainActivityViewModel @ViewModelInject constructor(
        @Assisted private val savedStateHandle: SavedStateHandle,
        // @Assisted private val context: Context
): ViewModel() {
    fun log(message: String){
        // Toast.makeText(context, "viewModel: $message", Toast.LENGTH_LONG).show()
    }
}