package com.example.android.service

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.PowerManager
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.example.android.service.databinding.ActivityFullScreenBinding

class FullscreenActivity : AppCompatActivity() {
    private var _mBinding: ActivityFullScreenBinding? = null
    private val mBinding get() = _mBinding!!

    private var mWakeLock: PowerManager.WakeLock? = null

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        mWakeLock = powerManager.newWakeLock(
            PowerManager.SCREEN_BRIGHT_WAKE_LOCK
                    or PowerManager.ACQUIRE_CAUSES_WAKEUP,
            "FullScreenActivity::WAKE_LOCK"
        ).apply {
            acquire()
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
        } else {
            window.addFlags(
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                        or WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _mBinding = ActivityFullScreenBinding.inflate(layoutInflater, null, false)

        mBinding.apply {
            buttonStop.setOnClickListener {
                stopService(Intent(applicationContext, FullScreenService::class.java))
                finishAffinity()
            }
        }

        setContentView(mBinding.root)
    }

    private fun releaseWakeLock() {
        mWakeLock?.let {
            if(it.isHeld) it.release()
        }
        mWakeLock = null
    }


    companion object {
        private const val TAG = "FullscreenActivity"
    }
}