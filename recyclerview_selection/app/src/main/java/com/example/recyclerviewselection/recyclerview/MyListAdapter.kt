package com.example.recyclerviewselection.recyclerview

import android.view.ViewGroup
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.widget.ListAdapter
import com.example.recyclerviewselection.models.MyItem

internal class MyListAdapter : ListAdapter<MyItem, MyViewHolder>(MyDiffUtil()) {

    var tracker: SelectionTracker<String>? = null

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(getItem(position), tracker?.isSelected(getItem(position).id) ?: false)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    fun getPosition(key: String): Int {
        return currentList.indexOfFirst { it.id == key }
    }
}