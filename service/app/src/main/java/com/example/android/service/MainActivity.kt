package com.example.android.service

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.android.service.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity(), SharedPreferences.OnSharedPreferenceChangeListener {

    private var _mBinding: ActivityMainBinding? = null
    private val mBinding get() = _mBinding!!
    private val mSharedPreferences by lazy {
        applicationContext.getSharedPreferences(
            ScheduledService.NAME_SHARED_PREF, Context.MODE_PRIVATE
        )
    }

    val mLogger = MyLogger(this@MainActivity, "MainActivity")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _mBinding = ActivityMainBinding.inflate(layoutInflater, null, false)

        mBinding.apply {
            buttonStartMain.setOnClickListener {
                val serviceIntent = Intent(applicationContext, MainActivityService::class.java)
                startServiceCompat(serviceIntent)
            }

            buttonStartFullscreen.setOnClickListener {
                val serviceIntent = FullScreenService.createStartIntent(this@MainActivity)
                startServiceCompat(serviceIntent)
            }

            buttonSchedule.setOnClickListener {
                val durationInMill = sliderDuration.value.toLong() * 1000
                val resetIntent = ScheduledService.createResetServiceIntent(this@MainActivity)
                val serviceIntent =
                    ScheduledService.createCountUpServiceIntent(this@MainActivity, durationInMill)

                startServiceCompat(resetIntent)
                startServiceCompat(serviceIntent)
            }

            buttonSchedulePerMin.setOnClickListener {
                val durationInMill = sliderDuration.value.toLong() * 1000
                val resetIntent =
                    ScheduledServicePerOneMin.createResetServiceIntent(this@MainActivity)
                val serviceIntent =
                    ScheduledServicePerOneMin.createStartServiceIntent(
                        this@MainActivity,
                        durationInMill
                    )

                startServiceCompat(resetIntent)
                startServiceCompat(serviceIntent)
            }
        }

        val startedAt = mSharedPreferences.getLong(ScheduledService.KEY_LAST_STARTED_AT, 0L)
        val updatedAt = mSharedPreferences.getLong(ScheduledService.KEY_UPDATED_AT, 0L)
        val elapsed = updatedAt - startedAt
        mBinding.textElapsed.text = "${elapsed / 1000}"
        mSharedPreferences.registerOnSharedPreferenceChangeListener(this)

        setContentView(mBinding.root)

        mBinding.buttonClear.setOnClickListener { mLogger.clear() }
        lifecycleScope.launch(Dispatchers.IO) {
            while (true) {
                val logs = mLogger.getLogs()
                runOnUiThread { mBinding.textLog.text = logs }
                delay(1000)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mSharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
    }

    private fun startServiceCompat(intent: Intent) {
        ContextCompat.startForegroundService(this, intent)
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