package com.example.tablayout

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class PageAdapter(fa: FragmentActivity): FragmentStateAdapter(fa) {
    override fun getItemCount(): Int {
        return 10
    }

    override fun createFragment(position: Int): Fragment {
        return PageFragment.newInstance(position)
    }
}