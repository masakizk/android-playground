package com.example.firebasestorage

import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.firebasestorage.databinding.FragmentFirstBinding
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.component1
import com.google.firebase.storage.ktx.component2

class FirstFragment : Fragment() {

    private lateinit var mStorage: FirebaseStorage
    private lateinit var mBinding: FragmentFirstBinding
    private val mSharedPreferences: SharedPreferences
        get() = requireActivity().getPreferences(Context.MODE_PRIVATE)


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mStorage = FirebaseStorage.getInstance()
        mBinding = FragmentFirstBinding.inflate(inflater, container, false).apply {
            btnDownloadImages.setOnClickListener { setImages() }
            btnUploadLocalImage.setOnClickListener { showSelectionTool() }
            btnDownloadImage.setOnClickListener { downloadImage() }
            btnDeleteImage.setOnClickListener { deleteImage() }
            btnListImages.setOnClickListener { listImages() }
        }

        return mBinding.root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            RESULT_PICK_IMAGE_FILE -> {
                if (resultCode != RESULT_OK) return
                data?.let {
                    val uri = it.data
                    uri?.let { uploadImage(it) }
                }
            }
        }
    }

    private fun setImages() {
        /**
         * ファイルのアップロード、ダウンロード、メタ情報の取得、更新を行うために参照を作成する
         * 参照は軽量なのでいくつでも作成することができ、再利用も可能。
         */
        val storageRef = mStorage.reference
        val imageRef = storageRef.child("images")
        val firebaseIconRef = storageRef.child("images/firebase.png")

        /**
         * 参照を利用して、階層内を移動することが可能。
         */
        val storageIconRef = imageRef.parent?.child("storage.png")
        val androidIconRef = imageRef.child("android.png")

        firebaseIconRef.downloadUrl.addOnCompleteListener {
            Glide.with(this).load(it.result).into(mBinding.imgFirebase)
        }

        storageIconRef?.downloadUrl?.addOnCompleteListener {
            Glide.with(this).load(it.result).into(mBinding.imgStorage)
        }

        androidIconRef.downloadUrl.addOnCompleteListener {
            Glide.with(this).load(it.result).into(mBinding.imgAndroid)
        }
    }

    private fun listImages() {
        val imgRef = mStorage.reference.child("images")
        imgRef.listAll()
            .addOnSuccessListener { (items, prefixes) ->
                prefixes.forEach { Log.d(TAG, "listImages: $it") }
                items.forEach { Log.d(TAG, "listImages: $it") }
            }
    }

    private fun downloadImage() {
        val reference = mStorage.reference.child("images").child("firebase.png")
        DownloadImage.downloadLocal(reference, requireContext().applicationContext)
    }

    private fun uploadImage(uri: Uri) {
        val reference = mStorage.reference.child("images").child("local.jpg")

        val savedUri = mSharedPreferences.getString(KEY_SESSION_URI, null)
        var sessionUri = if (savedUri != null) Uri.parse(savedUri) else null

        val uploadTask = UploadImage
            .uploadImageFromLocalFile(reference, uri, sessionUri)
            .addOnProgressListener { (bytesTransferred, totalByteCount) ->
                // 進行状況を表示
                val progress = (100 * bytesTransferred) / totalByteCount
                mBinding.tvUploadProgress.text = progress.toString()
            }
            .addOnProgressListener { taskSnapshot ->
                // セッションURIを保存
                sessionUri = taskSnapshot.uploadSessionUri
                with(mSharedPreferences.edit()) {
                    putString(KEY_SESSION_URI, sessionUri.toString())
                    apply()
                }
            }
            .addOnCompleteListener {
                with(mSharedPreferences.edit()) {
                    putString(KEY_SESSION_URI, null)
                    apply()
                }
            }

        val downLoadUriTask = uploadTask
            .continueWithTask { task ->
                // アップロード終了後に、ダウンロードURLを取得
                if (!task.isSuccessful) {
                    task.exception?.let { throw it }
                }
                reference.downloadUrl
            }.addOnFailureListener {
                Toast.makeText(
                    requireContext(),
                    "Failed in downloading image: $it",
                    Toast.LENGTH_LONG
                ).show()
            }
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Glide.with(this).load(task.result).into(mBinding.imgLocal)
                }
            }
    }

    private fun deleteImage() {
        DeleteImage.deleteImage(
            mStorage.reference.child("images").child("local.jpg"),
            requireContext()
        )
    }


    private fun showSelectionTool() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            .addCategory(Intent.CATEGORY_OPENABLE)
            .setType("image/*")
        startActivityForResult(intent, RESULT_PICK_IMAGE_FILE)
    }

    companion object {
        private const val TAG = "FirstFragment"
        private const val KEY_SESSION_URI = "session_uri"
        private const val RESULT_PICK_IMAGE_FILE = 2000
    }
}