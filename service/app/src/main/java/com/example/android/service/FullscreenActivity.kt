package com.example.android.service

import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.example.android.service.databinding.ActivityFullScreenBinding

class FullscreenActivity : AppCompatActivity() {
    private var _mBinding: ActivityFullScreenBinding? = null
    private val mBinding get() = _mBinding!!

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

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


    companion object {
        private const val TAG = "FullscreenActivity"
    }
}