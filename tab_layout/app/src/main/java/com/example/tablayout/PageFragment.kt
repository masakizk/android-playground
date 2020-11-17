package com.example.tablayout

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.tablayout.databinding.FragmentPageBinding

class PageFragment: Fragment() {
    private lateinit var binding: FragmentPageBinding
    private val index: Int get() = arguments?.getInt(PAGE_INDEX) ?: 0

    companion object{
        private const val PAGE_INDEX = "page_index"

        fun newInstance(index: Int): PageFragment {
            val fragment = PageFragment()
            fragment.arguments = Bundle().apply {
                putInt(PAGE_INDEX, index)
            }
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPageBinding.inflate(inflater, container, false)
        binding.pageTitle.text = index.toString()
        return binding.root
    }
}