package com.example.firebasestorage

import android.content.Context
import android.os.Environment
import android.util.Log
import com.google.firebase.storage.StorageReference
import java.io.File


object DownloadImage {
    /**
     * getFile ローカルデバイスに直接ダウンロードする
     */
    fun downloadLocal(imgRef: StorageReference, context: Context) {
        val path = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)?.toString()
        val file = File(path, imgRef.name)

        imgRef.getFile(file)
            .addOnSuccessListener { Log.d(TAG, "downloadLocal: Image is downloaded successfully") }
            .addOnFailureListener { Log.e(TAG, "Failed in downloading image: $it") }
    }

    fun downloadUrl(imageRf: StorageReference) {
        imageRf.downloadUrl
            .addOnSuccessListener { Log.d(TAG, "downloadUrl: $it") }
            .addOnFailureListener { Log.e(TAG, "Failed get download url: $it") }
    }

    private const val TAG = "DownloadImage"
}