package com.example.recyclerviewselection.recyclerview

import androidx.recyclerview.widget.DiffUtil
import com.example.recyclerviewselection.models.MyItem

class MyDiffUtil : DiffUtil.ItemCallback<MyItem>() {
    override fun areItemsTheSame(oldItem: MyItem, newItem: MyItem): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: MyItem, newItem: MyItem): Boolean {
        return oldItem == newItem
    }

}