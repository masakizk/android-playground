package com.example.recyclerviewselection.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.selection.SelectionPredicates
import androidx.recyclerview.selection.SelectionTracker
import androidx.recyclerview.selection.StorageStrategy
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.recyclerviewselection.databinding.FragmentFirstBinding
import com.example.recyclerviewselection.models.MyItem
import com.example.recyclerviewselection.recyclerview.MyDetailsLookup
import com.example.recyclerviewselection.recyclerview.MyListAdapter
import com.example.recyclerviewselection.recyclerview.StableIdKeyProvider

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    private lateinit var binding: FragmentFirstBinding
    private lateinit var adapter: MyListAdapter
    private lateinit var tracker: SelectionTracker<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFirstBinding.inflate(inflater, container, false)

        binding.apply {
            adapter = MyListAdapter()
            recyclerView.layoutManager = LinearLayoutManager(context)
            recyclerView.adapter = adapter

            tracker = SelectionTracker.Builder(
                "my_list_id",              // selection Id: activity, fragmentのコンテキスト内でselectionを識別するための文字列
                recyclerView,                         // recyclerView: trackerを適用するRecyclerView
                StableIdKeyProvider(adapter),         // key provider: selection keyのソース
                MyDetailsLookup(recyclerView),        // details lookup: RecyclerViewの項目についての情報源
                StorageStrategy.createStringStorage() // storage: selectionの状態の型安全なストレージの戦略
            ).withSelectionPredicate(
                SelectionPredicates.createSelectAnything() // 複数項目を制限なしに選択できるようにする。
            ).build()
            tracker.onRestoreInstanceState(savedInstanceState)
            adapter.tracker = tracker
        }

        tracker.addObserver(
            object : SelectionTracker.SelectionObserver<String>() {
                override fun onSelectionChanged() {
                    super.onSelectionChanged()
                    Log.d(TAG, "onSelectionChanged: ${tracker.selection}")
                }
            }
        )

        val items = List(10) { MyItem(it.toString(), it.toString()) }
        adapter.submitList(items)

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        tracker.onSaveInstanceState(outState)
    }

    companion object{
        private const val TAG = "FirstFragment"
    }
}