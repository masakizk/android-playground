package com.example.mediastore.ui

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.mediastore.R

/**
 * [MainActivity]では、メディアにアクセスする許可をもらう
 */
class MainActivity : AppCompatActivity() {
    private val requestPermissionForResult =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {

        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // READ_EXTERNAL_STORAGEはAndroid 6.0 Runtime Permissionに該当
        if (Build.VERSION.SDK_INT >= 23)
            requestPermissionForResult.launch(
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            )
    }
}