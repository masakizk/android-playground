package com.example.recyclerviewselection.recyclerview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.recyclerviewselection.databinding.ListItemBinding

internal class MyViewHolder(
    private val binding: ListItemBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(message: String) {
        binding.message.text = message
    }

    companion object {
        fun from(parent: ViewGroup): MyViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = ListItemBinding.inflate(inflater, parent, false)
            return MyViewHolder(binding)
        }
    }
}