package com.example.mediastore.ui

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.mediastore.BuildConfig
import com.example.mediastore.databinding.FragmentTakePhotoBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.*

/**
 * 画像を撮影し、ユーザーが閲覧しやすいギャラリーに保存する。
 * 手順
 * 1. Intentによりカメラアプリを起動
 *   a. 写真の保存先をアプリ固有のストレージに指定
 * 2. 撮影したファイルをMedia Storeを用いて、ギャラリーに保存
 *   a. ContentResolver#insert() を使って、データを追加する(対応するファイルのUriを受け取れる)
 *   b. アプリ固有のストレージから写真を読み込み、ギャラリーにコピーする。(ContentResolver#openOutputStreamを用いる)
 *   c. アプリ固有のストレージから写真を読み込み、サイズを取得する。
 *   d. MediaStoreの更新日と、ファイルサイズを修正する。
 */
class TakePhotoFragment : Fragment() {

    private val takePhotoForResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode != Activity.RESULT_OK) return@registerForActivityResult
            if (it?.data?.extras != null) {
                // サムネイル画質（画像の保存を指定したときは、nullになる）
                val imageBitmap = it.data!!.extras!!.get("data") as Bitmap
                binding.imageView.setImageBitmap(imageBitmap)
            } else if (mPhotoUrl != null) {
                // フルサイズの写真
                binding.textPath.text = mPhotoUrl.toString()
                binding.imageView.setImageURI(mPhotoUrl)

                lifecycleScope.launch {
                    savePhotoToGallery(mPhotoFile!!)
                }
            }
        }

    private lateinit var binding: FragmentTakePhotoBinding
    private var mPhotoUrl: Uri? = null
    private var mPhotoFile: File? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTakePhotoBinding.inflate(inflater, container, false)

        binding.apply {
            buttonTakePhoto.setOnClickListener { startCameraActivity() }
        }

        return binding.root
    }

    private fun createImageFile(): File {
        val storageDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "${Calendar.getInstance().timeInMillis}",
            ".jpg",
            storageDir
        )
    }

    private fun startCameraActivity() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        // 保存先を指定
        val file = createImageFile()
        mPhotoFile = file
        val photoURI = FileProvider.getUriForFile(
            requireContext(),
            "${BuildConfig.APPLICATION_ID}.fileprovider",
            file
        )
        mPhotoUrl = photoURI
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)

        // カメラを起動
        takePhotoForResult.launch(intent)
    }

    private suspend fun savePhotoToGallery(file: File) = withContext(Dispatchers.IO) {
        val resolver = requireContext().contentResolver
        val fileName = "${Calendar.getInstance().timeInMillis}.jpg"

        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        }

        val imageDirectory = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            ?: MediaStore.Images.Media.INTERNAL_CONTENT_URI

        val uri = resolver.insert(imageDirectory, contentValues)!!

        // 画像をアプリ固有のストレージから、ギャラリーにコピー
        resolver.openOutputStream(uri)?.use { output ->
            file.inputStream().use { input ->
                input.copyTo(output)
            }
        }
        file.deleteOnExit()

        // 画像サイズを変更
        contentValues.apply {
            put(MediaStore.Images.Media.SIZE, file.length())
            val now = System.currentTimeMillis() / 1000
            put(MediaStore.Images.Media.DATE_ADDED, now)
            put(MediaStore.Images.Media.DATE_MODIFIED, now)
            put(MediaStore.Images.Media.DATE_TAKEN, now)
        }
        requireContext().contentResolver.update(
            uri,
            contentValues,
            null,
            null
        )
    }
}