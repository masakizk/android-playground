package com.example.android.service

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.android.service.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), SharedPreferences.OnSharedPreferenceChangeListener {

    private var _mBinding: ActivityMainBinding? = null
    private val mBinding get() = _mBinding!!
    private val mSharedPreferences by lazy {
        applicationContext.getSharedPreferences(
            ScheduledService.NAME_SHARED_PREF, Context.MODE_PRIVATE
        )
    }

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

            buttonSchedule.setOnClickListener {
                val durationInMill = sliderDuration.value.toLong() * 1000
                val resetIntent = ScheduledService.createResetServiceIntent(this@MainActivity)
                val serviceIntent =
                    ScheduledService.createCountUpServiceIntent(this@MainActivity, durationInMill)

                if (VERSION.SDK_INT >= VERSION_CODES.O) {
                    startForegroundService(resetIntent)
                    startForegroundService(serviceIntent)
                } else {
                    startService(resetIntent)
                    startService(serviceIntent)
                }
            }
        }

        val startedAt = mSharedPreferences.getLong(ScheduledService.KEY_LAST_STARTED_AT, 0L)
        val updatedAt = mSharedPreferences.getLong(ScheduledService.KEY_UPDATED_AT, 0L)
        val elapsed = updatedAt - startedAt
        mBinding.textElapsed.text = "${elapsed / 1000}"
        mSharedPreferences.registerOnSharedPreferenceChangeListener(this)

        setContentView(mBinding.root)
    }

    override fun onDestroy() {
        super.onDestroy()
        mSharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
    }

    companion object {
        private const val TAG = "MainActivity"
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        if (sharedPreferences == null) return
        when (key) {
            ScheduledService.KEY_UPDATED_AT -> {
                val startedAt = sharedPreferences.getLong(ScheduledService.KEY_LAST_STARTED_AT, 0L)
                val updatedAt = sharedPreferences.getLong(ScheduledService.KEY_UPDATED_AT, 0L)
                val elapsed = updatedAt - startedAt
                runOnUiThread { mBinding.textElapsed.text = "${elapsed / 1000}" }
            }
        }
    }
}