package com.example.android.herotransition

import android.Manifest
import android.content.ContentUris
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.transition.TransitionInflater
import android.transition.TransitionSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.SharedElementCallback
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import com.example.android.herotransition.databinding.FragmentFirstBinding
import com.example.android.herotransition.list.MyItem
import com.example.android.herotransition.list.MyItemListAdapter
import com.example.android.herotransition.list.MyItemViewHolder


class FirstFragment : Fragment(), MyItemViewHolder.CallbackListener {

    private var _binding: FragmentFirstBinding? = null
    private val binding get() = _binding!!
    private val adapter = MyItemListAdapter(this)

    companion object {
        private const val TAG = "FirstFragment"
        private var currentPosition = 0
    }

    private val requestPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            val photos = readLocalImages()
            val items = photos.mapIndexed { i, uri -> MyItem(id = i, uri) }
            adapter.submitList(items)

            // 全ての要素が揃ったあとにアニメーションを実行する
            (view?.parent as? ViewGroup)?.doOnPreDraw {
                startPostponedEnterTransition()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFirstBinding.inflate(inflater, container, false).apply {
            recyclerListView.adapter = adapter
        }

        requestPermission.launch(Manifest.permission.READ_EXTERNAL_STORAGE)

        postponeEnterTransition()

        // 遷移アニメーションの指定
        exitTransition = TransitionInflater.from(requireContext())
            .inflateTransition(R.transition.exit_transition)

        // 遷移元と遷移先の共通要素のマッピングをする
        // 遷移元のフラグメントを離れるときと、「戻る」のナビゲーションのときに呼ばれる
        setExitSharedElementCallback(object : SharedElementCallback() {
            override fun onMapSharedElements(
                names: MutableList<String>,
                sharedElements: MutableMap<String, View>
            ) {
                val id = names[0]
                val position = adapter.currentList.indexOfFirst { it.id.toString() == id }
                val selectedViewHolder = binding.recyclerListView.findViewHolderForAdapterPosition(position)
                if(selectedViewHolder?.itemView == null) return
                sharedElements[id] = selectedViewHolder.itemView.findViewById(R.id.image_view)
            }
        })

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        postponeEnterTransition()
    }

    private fun readLocalImages(): List<Uri> {
        val photos = mutableListOf<Uri>()

        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.ImageColumns.DATE_TAKEN,
        )

        val cursor = requireContext().contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            null,
            null,
            MediaStore.Images.ImageColumns.DATE_TAKEN + " DESC"
        )

        kotlin.runCatching {
            if (cursor != null) {
                cursor.moveToFirst()
                do {
                    val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                    val id = cursor.getLong(idColumn)
                    val contentUri = ContentUris.withAppendedId(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        id
                    )
                    photos.add(contentUri)
                } while (cursor.moveToNext())
                cursor.close()
            }
        }

        return photos
    }

    override fun onCLick(item: MyItem, view: View) {
        currentPosition = item.id

        (exitTransition as TransitionSet).excludeTarget(view, true)

        val extras = FragmentNavigatorExtras(view to "hero_image")
        val args = SecondFragmentArgs(imageUrl = item.uri.toString())
        findNavController().navigate(
            R.id.action_first_to_second,
            args.toBundle(),
            null,
            extras
        )
    }
}