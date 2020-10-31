package com.example.daggerhilt

import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import com.example.apple.FirstFragmentDirections
import com.example.banana.SecondFragment
import com.example.banana.SecondFragmentArgs
import com.example.banana.SecondFragmentDirections
import com.example.core.Router

class ApplicationRouter : Router {
    override fun navigateToSecondFragment(controller: NavController, message: String) {
        controller.navigate(
            FirstFragmentDirections.actionSecondFragment(
                message = message
            )
        )
    }

    override fun navigateToFirstFragment(controller: NavController, message: String) {
        controller.navigate(
            SecondFragmentDirections.actionFirstFragment()
        )
    }

    override fun newSecondFragment(message: String): Fragment {
        val args = SecondFragmentArgs(message = message)
        return SecondFragment.newInstance(args)
    }
}