package com.example.android.herotransition.list

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter

class MyItemListAdapter(
    private val listener: MyItemViewHolder.CallbackListener
) : ListAdapter<MyItem, MyItemViewHolder>(MyItemDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyItemViewHolder {
        return MyItemViewHolder.from(parent, listener)
    }

    override fun onBindViewHolder(holder: MyItemViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}