package com.example.recyclerviewselection.recyclerview

import android.view.MotionEvent
import androidx.recyclerview.selection.ItemDetailsLookup
import androidx.recyclerview.widget.RecyclerView

/**
 * ユーザーが選択した項目の情報を selection library に教える。
 * 選択はMoveEventに基づき、ViewHolderをマッピングする。
 */
class MyDetailsLookup(
    private val recyclerView: RecyclerView
) : ItemDetailsLookup<String>() {

    override fun getItemDetails(e: MotionEvent): ItemDetails<String>? {
        val view = recyclerView.findChildViewUnder(e.x, e.y) ?: return null
        val holder = recyclerView.getChildViewHolder(view) as MyViewHolder
        return holder.getItemDetails()
    }
}