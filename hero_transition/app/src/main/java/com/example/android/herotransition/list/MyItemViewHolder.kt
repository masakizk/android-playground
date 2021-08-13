package com.example.android.herotransition.list

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.android.herotransition.databinding.ListItemBinding

class MyItemViewHolder(
    private val mBinding: ListItemBinding,
    private val mListener: CallbackListener,
) : RecyclerView.ViewHolder(mBinding.root) {

    interface CallbackListener {
        fun onCLick(item: MyItem, view: View)
    }

    fun bind(data: MyItem) {
        ViewCompat.setTransitionName(mBinding.imageView, data.id.toString())

        Glide.with(mBinding.root.context)
            .load(data.uri)
            .into(mBinding.imageView)

        mBinding.imageView.setOnClickListener {
            mListener.onCLick(data, mBinding.imageView)
        }
    }

    companion object {
        fun from(parent: ViewGroup, listener: CallbackListener): MyItemViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = ListItemBinding.inflate(inflater, parent, false)
            return MyItemViewHolder(binding, listener)
        }
    }
}