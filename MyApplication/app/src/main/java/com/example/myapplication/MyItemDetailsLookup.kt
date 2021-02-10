package com.example.myapplication

import android.view.MotionEvent
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.widget.RecyclerView

class MyItemDetailsLookup(
        private val recyclerView: RecyclerView
): ItemDetailsLookup<Long>(){
    override fun getItemDetails(e: MotionEvent): ItemDetails<Long>? {
        val view = recyclerView.findChildViewUnder(e.x, e.y) ?: return null
        val holder = recyclerView.getChildViewHolder(view) as MyAdapter.ViewHolder
        return holder.getItemDetails()
    }
}