package com.example.myapplication

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import java.util.*

class MyItemTouchCallback(
    private val data: MutableLiveData<List<Int>>
) : ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP or ItemTouchHelper.DOWN, 0) {
    companion object {
        private const val TAG = "MyItemTouchCallback"
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        val adapter = recyclerView.adapter as MyAdapter
        val from = viewHolder.adapterPosition
        val to = target.adapterPosition
        Log.d(TAG, "onMove: $from -> $to")
        Collections.swap(data.value, from, to)
        adapter.notifyItemMoved(from, to)
        return true
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

    }
}