package com.example.firebasestorage

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.util.Log
import android.widget.ImageView
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.google.firebase.storage.ktx.storageMetadata
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream

object UploadImage {
    /**
     * putByes()によって、byte[]を受け取ってUploadTaskを作成。
     * これを使用して、アップロードステータスの管理、モニタリングが可能。
     * アプリはファイルのコンテンツ全体を一度メモリ内に保持する必要がある。
     */
    fun uploadImageFromMemory(
        imgReference: StorageReference,
        imageView: ImageView
    ): UploadTask {
        val bitmap = (imageView.drawable as BitmapDrawable).bitmap
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        return imgReference.putBytes(data).apply {
            addOnFailureListener {
                Log.e(TAG, "Failed in uploading image: $it")
            }
            addOnSuccessListener { taskSnapshot ->
                Log.e(TAG, "Uploaded image successfully")
                Log.d(TAG, "uploadImage: ${taskSnapshot.metadata.toString()}")
            }
        }
    }

    /**
     * Cloud Storageにアップロードする上で最も用途の広い方法。
     */
    fun uploadImageFromStream(imgReference: StorageReference, file: File): UploadTask {
        val stream = FileInputStream(file)

        return imgReference.putStream(stream).apply {
            addOnFailureListener {
                Log.e(TAG, "Failed in uploading image: $it")
            }
            addOnSuccessListener { taskSnapshot ->
                Log.e(TAG, "Uploaded image successfully")
                Log.d(TAG, "uploadImage: ${taskSnapshot.metadata.toString()}")
            }
        }
    }

    /**
     * 写真や動画などのローカルファイルをputFile()メソッドを利用してアップロード
     */
    fun uploadImageFromLocalFile(
        imgReference: StorageReference,
        pathName: String
    ): UploadTask {
        val metaData = storageMetadata {
            contentType = "image/jpg"
        } // メタデータを追加する(任意)
        val file = Uri.fromFile(File(pathName))

        return imgReference.putFile(file, metaData).apply {
            addOnFailureListener {
                Log.e(TAG, "Failed in uploading image: $it")
            }
            addOnSuccessListener { taskSnapshot ->
                Log.e(TAG, "Image is uploaded successfully")
                Log.d(TAG, "uploadImage: ${taskSnapshot.metadata?.name}")
            }
        }
    }

    fun uploadImageFromLocalFile(
        imgReference: StorageReference,
        uri: Uri,
        sessionUri: Uri?
    ): UploadTask {
        return imgReference.putFile(uri, storageMetadata {}, sessionUri).apply {
            addOnFailureListener {
                Log.e(TAG, "Failed in uploading image: $it")
            }
            addOnSuccessListener { taskSnapshot ->
                Log.e(TAG, "Uploaded image successfully")
                Log.d(TAG, "uploadImage: ${taskSnapshot.metadata.toString()}")
            }
        }
    }

    fun getDownloadUrl(imageReference: StorageReference, uploadTask: UploadTask) {
        uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let { throw it }
            }
            imageReference.downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) Log.d(TAG, "getDownloadUrl: ${task.result}")
        }
    }

    private const val TAG = "UploadImage"
}