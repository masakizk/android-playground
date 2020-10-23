package com.example.mediastore.viewmodel

import android.content.ContentResolver
import android.content.ContentUris
import android.provider.MediaStore
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mediastore.model.MediaStoreImage
import com.example.mediastore.util.Utils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import java.util.concurrent.TimeUnit

class ImageViewModel : ViewModel() {

    companion object {
        private const val TAG = "MainActivityViewModel"
    }

    suspend fun loadImages(contentResolver: ContentResolver): List<MediaStoreImage> {
        return queryImages(contentResolver)
    }

    private suspend fun queryImages(contentResolver: ContentResolver): List<MediaStoreImage> {
        val images = mutableListOf<MediaStoreImage>()
        // 取得するカラム
        // nullを指定するとすべてのカラムが返されるので非効率
        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.DATE_ADDED
        )

        // SQLのwhere句と同じフォーマット(WHEREは省く)
        // 指定がないとすべての行を返す
        val selection = "${MediaStore.Images.Media.DATE_ADDED} >= ?"

        // sectionに "?" をつけた場合、配列の要素が順に置き換えられる
        val selectionArgs = arrayOf(
            Utils.dateToTimestamp(day = 22, month = 10, year = 2008).toString()
        )

        // SQLのORDER BY と同じフォーマットで
        // 順番を指定することができる
        val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"

        val cursor = contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            sortOrder
        )

        withContext(Dispatchers.IO) {
            cursor?.use { cursor ->
                // ファイルに関する情報の列
                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                val dateModifiedColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED)
                val displayNameColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
                Log.i(TAG, "Found ${cursor.count} images")

                while (cursor.moveToNext()) {
                    // カラムからファイルに関する情報を取り出す
                    val id = cursor.getLong(idColumn)
                    val dateModified = Date(TimeUnit.SECONDS.toMillis(cursor.getLong(dateModifiedColumn)))
                    val displayName = cursor.getString(displayNameColumn)

                    // 取得したメディアidを,パスに追加してURIを取得する
                    val contentUri = ContentUris.withAppendedId(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        id
                    )

                    val image = MediaStoreImage(id, displayName, dateModified, contentUri)
                    images += image

                    // For debugging, we'll output the image objects we create to logcat.
                    Log.v(TAG, "Added image: $image")
                }
            }
        }

        return images
    }
}