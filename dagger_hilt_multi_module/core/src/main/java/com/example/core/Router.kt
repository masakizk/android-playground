package com.example.core

import androidx.fragment.app.Fragment
import androidx.navigation.NavController

interface Router {
    fun navigateToSecondFragment(controller: NavController, message: String)

    fun navigateToFirstFragment(controller: NavController)

    fun newSecondFragment(message: String): Fragment
}