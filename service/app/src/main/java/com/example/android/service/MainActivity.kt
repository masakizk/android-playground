package com.example.android.service

import android.content.Intent
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.android.service.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private var _mBinding: ActivityMainBinding? = null
    private val mBinding get() = _mBinding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _mBinding = ActivityMainBinding.inflate(layoutInflater, null, false)

        mBinding.apply {
            buttonStartMain.setOnClickListener {
                val serviceIntent = Intent(applicationContext, MainActivityService::class.java)

                if (VERSION.SDK_INT >= VERSION_CODES.O) {
                    startForegroundService(serviceIntent)
                } else {
                    startService(serviceIntent)
                }
            }

            buttonStartFullscreen.setOnClickListener {
                val serviceIntent = Intent(applicationContext, FullScreenService::class.java)

                if (VERSION.SDK_INT >= VERSION_CODES.O) {
                    startForegroundService(serviceIntent)
                } else {
                    startService(serviceIntent)
                }
            }
        }

        setContentView(mBinding.root)
    }
}