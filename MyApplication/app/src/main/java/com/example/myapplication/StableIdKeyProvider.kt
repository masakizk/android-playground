package com.example.myapplication

import androidx.recyclerview.selection.ItemKeyProvider
import androidx.recyclerview.widget.RecyclerView

class StableIdKeyProvider(
        private val recyclerView: RecyclerView
) : ItemKeyProvider<Long>(SCOPE_MAPPED) {
    override fun getKey(position: Int): Long? {
        return recyclerView.adapter?.getItemId(position)
    }

    override fun getPosition(key: Long): Int {
        val holder = recyclerView.findViewHolderForItemId(key)
        return holder?.adapterPosition ?: -1
    }
}