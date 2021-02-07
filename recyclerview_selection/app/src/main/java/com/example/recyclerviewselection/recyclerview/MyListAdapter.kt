package com.example.recyclerviewselection.recyclerview

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter

internal class MyListAdapter : ListAdapter<String, MyViewHolder>(MyDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}