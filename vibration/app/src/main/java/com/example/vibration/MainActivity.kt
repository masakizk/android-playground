package com.example.vibration

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.appcompat.app.AppCompatActivity
import com.example.vibration.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var vibrator: Vibrator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

    }

    override fun onStart() {
        super.onStart()
        binding.apply {
            vibrateOnce.setOnClickListener { vibrateOnce() }
            vibrateTwice.setOnClickListener { vibrateTwice() }
            vibrateRepeatedly.setOnClickListener { vibrateRepeatedly() }
            stopVibrate.setOnClickListener { stopVibrate() }
        }
    }

    private fun vibrateOnce() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val vibrationEffect = VibrationEffect.createOneShot(300, VibrationEffect.DEFAULT_AMPLITUDE)
            vibrator.vibrate(vibrationEffect)
        } else {
            vibrator.vibrate(300)
        }
    }

    private fun vibrateTwice() {
        // repeatに -1 を指定するとリピートしない
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val vibrationEffect = VibrationEffect.createWaveform(
                    longArrayOf(0, 500, 400, 200),
                    intArrayOf(0, VibrationEffect.DEFAULT_AMPLITUDE, 0, VibrationEffect.DEFAULT_AMPLITUDE),
                    -1
            )
            vibrator.vibrate(vibrationEffect)
        } else {
            vibrator.vibrate(longArrayOf(0, 500, 400, 200), -1)
        }
    }

    private fun vibrateRepeatedly(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val vibrationEffect = VibrationEffect.createWaveform(
                    longArrayOf(0, 500, 400, 200),
                    intArrayOf(0, VibrationEffect.DEFAULT_AMPLITUDE, 0, VibrationEffect.DEFAULT_AMPLITUDE),
                    2
            )
            vibrator.vibrate(vibrationEffect)
        } else {
            vibrator.vibrate(longArrayOf(0, 500, 400, 200), 2)
        }
    }

    private fun stopVibrate(){
        vibrator.cancel()
    }
}