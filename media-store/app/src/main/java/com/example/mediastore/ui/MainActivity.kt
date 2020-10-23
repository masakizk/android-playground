package com.example.mediastore.ui

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker.PERMISSION_GRANTED
import com.example.mediastore.R

/**
 * [MainActivity]では、メディアにアクセスする許可をもらう
 */
class MainActivity : AppCompatActivity() {
    companion object {
        private const val READ_EXTERNAL_STORAGE_REQUEST = 100
        private const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // READ_EXTERNAL_STORAGEはAndroid 6.0 Runtime Permissionに該当
        if (Build.VERSION.SDK_INT >= 23)
            requestPermission()
        else
            showImageViewFragment()
    }

    /**
     * 現在のPermissionの状態を確認
     * もし許可をもらっていたらtrueを返し、そうでなければfalseを返す
     */
    private fun haveStoragePermission(): Boolean {
        val permission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
        return permission == PERMISSION_GRANTED
    }

    /**
     * ユーザーに対し、ダイアログを表示し、
     * 外部ストレージへのアクセス権限をもらう
     */
    private fun requestPermission() {
        if (haveStoragePermission()) {
            showImageViewFragment()
        } else {
            val permissions = arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            ActivityCompat.requestPermissions(this, permissions, READ_EXTERNAL_STORAGE_REQUEST)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            READ_EXTERNAL_STORAGE_REQUEST -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PERMISSION_GRANTED) {
                    // キャンセルされたとき、空の配列を渡される
                    showImageViewFragment()
                } else {
                    // 許可を求める
                    val showRationale = ActivityCompat.shouldShowRequestPermissionRationale(
                        this,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    )

                    if (showRationale) showImageViewFragment()
                    else Log.d(TAG, "onRequestPermissionsResult: User clicked Don't show again.")
                }
            }
        }
    }

    private fun showImageViewFragment() {
        val fragment = ImageViewFragment()
        supportFragmentManager.beginTransaction()
            .add(R.id.image_view_container, fragment)
            .commit()
    }
}