package com.example.alarmmanager

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import androidx.appcompat.app.AppCompatActivity
import com.example.alarmmanager.databinding.ActivityMainBinding
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var alarmMgr: AlarmManager
    private lateinit var alarmIntent: PendingIntent

    private lateinit var binding: ActivityMainBinding

    private val requestCode = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 再起動時にレシーバーを呼び出せるようにする
        AlarmReceiver.enableReceiver(this)

        alarmMgr = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmIntent = Intent(this, AlarmReceiver::class.java).let { intent ->
            PendingIntent.getBroadcast(this, 0, intent, 0)
        }


        binding.apply {
            elapsedRealtime.setOnClickListener { elapsedRealtime() }
            elapsedRealtimeWakeup.setOnClickListener { elapsedRealtimeWakeup() }
            rtc.setOnClickListener { rtc() }
            rtcWakeup.setOnClickListener { rtcWakeup() }
            cancel.setOnClickListener { cancel() }
            inexactRepeat.setOnClickListener { inexactRepeat() }
            exactRepeat.setOnClickListener { exactRepeat() }
        }
    }


    // ELAPSED_REALTIME
    // デバイス起動してから指定した時間が経過したあと
    // ペンディングインテントを開始
    private fun elapsedRealtime() {
        alarmMgr.set(
            AlarmManager.ELAPSED_REALTIME,
            SystemClock.elapsedRealtime() + 10 * 1000,
            alarmIntent
        )
    }

    // ELAPSED_REALTIME_WAKEUP
    // デバイス起動してから指定した時間が経過したあと
    // デバイスのスリープを解除する、ペンディングインテントを開始
    private fun elapsedRealtimeWakeup() {
        alarmMgr.set(
            AlarmManager.ELAPSED_REALTIME_WAKEUP,
            SystemClock.elapsedRealtime() + 10 * 1000,
            alarmIntent
        )
    }

    // RTC
    // 指定された時間に
    // ペンディングインテントを開始
    private fun rtc(){
        val calendar: Calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis() + 10*1000
        }

        alarmMgr.set(
            AlarmManager.RTC,
            calendar.timeInMillis,
            alarmIntent
        )
    }

    // RTC_WAKEUP
    // 指定した時間に
    // デバイスのスリープを解除、ペンディングインテントを開始
    private fun rtcWakeup(){
        val calendar: Calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis() + 10*1000
        }

        alarmMgr.set(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            alarmIntent
        )
    }

    private fun cancel(){
        alarmMgr.cancel(alarmIntent)
    }

    // 厳密でないリピート
    // 複数の不正確なアラームが同期され、同時にトリガーされる
    // 電池消耗を抑えることができる。
    private fun inexactRepeat() {
        alarmMgr.setInexactRepeating(
            AlarmManager.ELAPSED_REALTIME,
            SystemClock.elapsedRealtime() + 10 * 1000,
            AlarmManager.INTERVAL_HALF_HOUR,
            alarmIntent
        )
    }

    private fun exactRepeat() {
        alarmMgr.setRepeating(
            AlarmManager.ELAPSED_REALTIME,
            SystemClock.elapsedRealtime() + 10 * 1000,
             60 * 1000,
            alarmIntent
        )
    }
}