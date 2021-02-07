package com.example.recyclerviewselection.recyclerview

import androidx.recyclerview.selection.ItemKeyProvider
import androidx.recyclerview.widget.RecyclerView

/**
 * KeyProviderの構築に使用する selection item　のキータイプを決定する。
 * selectionライブラリは以下の3つのタイプをサポートしている。
 * - Parcelable
 *      Uriと組み合わせて使うときに適している。
 * - String
 *      文字列のIDに対して利用可能。
 * - Long
 *      RecyclerViewのLong型のIDがすでに使われている場合に利用する。
 */
internal class StableIdKeyProvider(
    private val adapter: MyListAdapter
) : ItemKeyProvider<String>(SCOPE_MAPPED) {

    override fun getKey(position: Int): String? {
        return adapter.currentList[position].id
    }

    override fun getPosition(key: String): Int {
        return adapter.getPosition(key)
    }
}