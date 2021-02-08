package com.example.recyclerviewselection.recyclerview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.widget.RecyclerView
import com.example.recyclerviewselection.databinding.ListItemBinding
import com.example.recyclerviewselection.models.MyItem

internal class MyViewHolder(
    private val binding: ListItemBinding
) : RecyclerView.ViewHolder(binding.root) {

    private var id: String = ""

    fun bind(item: MyItem, isActivated: Boolean) {
        id = item.id
        binding.message.text = item.value
        itemView.isActivated = isActivated
    }

    fun getItemDetails(): ItemDetailsLookup.ItemDetails<String> {
        // ItemDetails抽象クラスを実装する。
        return object : ItemDetailsLookup.ItemDetails<String>() {
            override fun getPosition(): Int = adapterPosition
            override fun getSelectionKey(): String = id
        }
    }

    companion object {
        fun from(parent: ViewGroup): MyViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = ListItemBinding.inflate(inflater, parent, false)
            return MyViewHolder(binding)
        }
    }
}