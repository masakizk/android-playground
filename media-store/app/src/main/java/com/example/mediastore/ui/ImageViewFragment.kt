package com.example.mediastore.ui

import android.app.Activity.RESULT_OK
import android.content.ContentResolver
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.mediastore.databinding.FragmentImageViewBinding
import com.example.mediastore.viewmodel.ImageViewModel
import kotlinx.coroutines.launch

/**
 * [ImageViewFragment]では、2つの方法により画像を取得する
 * - 選択ツールUIによって、ユーザーが画像を選択
 * - クエリを利用して画像を取得する(ViewModel内で行っている)
 */
class ImageViewFragment : Fragment() {
    companion object {
        private const val RESULT_PICK_IMAGE_FILE = 1000
        private const val TAG = "ImageViewActivity"
    }

    private val viewModel: ImageViewModel by viewModels()
    private lateinit var binding: FragmentImageViewBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentImageViewBinding.inflate(layoutInflater)
        binding.apply {
            loadImageButton.setOnClickListener { loadImage() }
            showSelectTool.setOnClickListener { showSelectTool() }
        }

        return binding.root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            RESULT_PICK_IMAGE_FILE -> {
                if (resultCode != RESULT_OK) return
                data?.let {
                    val uri = it.data
                    if (uri != null) showImage(requireContext().contentResolver, uri)
                }
            }
        }
    }

    /**
     * 選択ツール(システムUI)を表示し、
     * [onActivityResult]で画像の[Uri]を受け取る
     */
    private fun showSelectTool() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            .addCategory(Intent.CATEGORY_OPENABLE)
            .setType("image/*")
        startActivityForResult(intent, RESULT_PICK_IMAGE_FILE)
    }

    // Intent でActivityへ移行
    private fun loadImage() {
        lifecycleScope.launch {
            val images = viewModel.loadImages(requireContext().contentResolver)
            if (images.isEmpty()) return@launch
            showImage(requireContext().contentResolver, images[0].uri)
        }
    }

    /**
     * 指定されたUriから画像を取得して、
     * 画面に画像とそのUriを表示する
     * @param uri 画像のUri
     */
    private fun showImage(contentResolver: ContentResolver, uri: Uri) {
        binding.fileUriText.text = uri.toString()

        val parcelFileDescriptor = contentResolver.openFileDescriptor(uri, "r")
        val fileDescriptor = parcelFileDescriptor!!.fileDescriptor
        val bmp = BitmapFactory.decodeFileDescriptor(fileDescriptor)
        parcelFileDescriptor.close()
        binding.thumbnailImage.setImageBitmap(bmp)
    }
}
