package com.example.myapplication

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.databinding.ItemBinding

class MyAdapter : ListAdapter<Int, MyAdapter.ViewHolder>(MyDiffUtil()) {

    init {
        setHasStableIds(true)
    }

    class ViewHolder(private val binding: ItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(value: Int) {
            binding.valueText.text = value.toString()
        }

        fun getItemDetails(): ItemDetailsLookup.ItemDetails<Long>? {
            return object : ItemDetailsLookup.ItemDetails<Long>() {
                override fun getPosition(): Int = adapterPosition
                override fun getSelectionKey(): Long = itemId
            }
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val binding =
                    ItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return ViewHolder(binding)
            }
        }
    }


    override fun getItemId(position: Int): Long {
        return getItem(position).toLong()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}