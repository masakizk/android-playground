package com.example.android.service

import android.app.KeyguardManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.example.android.service.databinding.ActivityFullScreenBinding

class FullscreenActivity : AppCompatActivity() {
    private var _mBinding: ActivityFullScreenBinding? = null
    private val mBinding get() = _mBinding!!

    private var mWakeLock: PowerManager.WakeLock? = null

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

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
        } else {
            window.addFlags(
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                        or WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
            )
        }

        with(getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                requestDismissKeyguard(this@FullscreenActivity, null)
            }
        }
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