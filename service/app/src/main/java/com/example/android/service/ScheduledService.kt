package com.example.android.service

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.SystemClock
import android.util.Log
import androidx.core.app.AlarmManagerCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*

/**
 * 指定された秒数カウントアップを続け、
 * それが終了しとき、スリープを解除しフルスクリーンのアクティビティを表示するサービス
 */
class ScheduledService : Service() {

    private lateinit var mAlarmManager: AlarmManager
    private lateinit var mNotificationManager: NotificationManagerCompat
    private val mSharedPreferences
        get() = applicationContext.getSharedPreferences(
            NAME_SHARED_PREF,
            Context.MODE_PRIVATE
        )
    private val startedAt
        get(): Long {
            if (mSharedPreferences.getLong(KEY_STARTED_AT, 0L) == 0L) {
                mSharedPreferences.edit {
                    putLong(KEY_STARTED_AT, Calendar.getInstance().timeInMillis)
                    putLong(KEY_LAST_STARTED_AT, Calendar.getInstance().timeInMillis)
                }
            }
            return mSharedPreferences.getLong(KEY_STARTED_AT, 0L)
        }

    private val mLogger get() = MyLogger(this, "ScheduledService")

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        mAlarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        mNotificationManager = NotificationManagerCompat.from(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_COUNT_UP -> {
                startForeground(NotificationConstants.NORMAL.ID, createNotification("starting"))
                val argDuration = intent.getLongExtra(ARG_DURATION, -1L)
                val savedDuration = mSharedPreferences.getLong(ARG_DURATION, -1L)
                if (argDuration == -1L && savedDuration == -1L) return START_NOT_STICKY
                val duration = if (argDuration != -1L) argDuration else savedDuration
                mSharedPreferences.edit { putLong(ARG_DURATION, duration) }

                scheduleFullscreen(applicationContext, duration)

                // 1秒おきに通知を更新する
                CoroutineScope(Dispatchers.Default).launch {
                    startTimer(duration)

                    stopForeground(true)
                    stopSelf(startId)
                }
//                startTimerWithThread(duration)

                return START_STICKY
            }

            ACTION_FULLSCREEN -> {
                Log.d(TAG, "onStartCommand: $ACTION_FULLSCREEN")
                val notification = createFullscreenNotification("DONE")
                mNotificationManager.cancel(NotificationConstants.HeadUp.ID)
                startForeground(NotificationConstants.HeadUp.ID, notification)

                mLogger.i("DONE")

                CoroutineScope(Dispatchers.Default).launch {
                    // 10秒後に自動停止
                    delay(10 * 1000L)
                    stopForeground(true)
                    stopSelf(startId)
                }
                return START_NOT_STICKY
            }

            ACTION_RESET -> {
                mSharedPreferences.edit {
                    remove(KEY_STARTED_AT)
                    remove(ARG_DURATION)
                }

                val fullscreen = createFullscreenServiceIntent(applicationContext)
                // AlarmManagerをキャンセル
                val pendingIntent = PendingIntent.getService(
                    applicationContext,
                    REQUEST_FULLSCREEN,
                    fullscreen,
                    PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_NO_CREATE
                )
                if (pendingIntent != null) {
                    val alarmManager =
                        applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                    alarmManager.cancel(pendingIntent)
                    pendingIntent.cancel()
                }
                return START_NOT_STICKY
            }
        }
        return START_NOT_STICKY
    }

    private suspend fun startTimer(duration: Long) {
        while (true) {
            val now = Calendar.getInstance().timeInMillis
            val elapsed = now - startedAt
            mLogger.i("startTimer - ${elapsed / 1000}")
            if (elapsed > duration) {
                ContextCompat.startForegroundService(
                    this,
                    createFullscreenServiceIntent(this)
                )
                break
            }

            val notification = createNotification("${elapsed / 1000}")
            mNotificationManager.cancel(NotificationConstants.NORMAL.ID) // 優先度が低いため、キャンセルしないと更新されない
            startForeground(NotificationConstants.NORMAL.ID, notification)

            mSharedPreferences.edit(commit = true) { putLong(KEY_UPDATED_AT, now) }
            delay(1000)
        }
    }

    private fun startTimerWithThread(duration: Long) {
        val handler = Handler(Looper.getMainLooper())
        val runner = object : Runnable {
            override fun run() {
                val now = Calendar.getInstance().timeInMillis
                val elapsed = now - startedAt
                mLogger.i("startTimer - ${elapsed / 1000}")
                if (elapsed > duration) {
                    stopSelf()
                    stopForeground(true)
                    return
                }

                val notification = createNotification("${elapsed / 1000}")
                mNotificationManager.cancel(NotificationConstants.NORMAL.ID) // 優先度が低いため、キャンセルしないと更新されない
                mNotificationManager.notify(NotificationConstants.NORMAL.ID, notification)

                mSharedPreferences.edit(commit = true) { putLong(KEY_UPDATED_AT, now) }
                handler.postDelayed(this, 1000)
            }
        }
        handler.post(runner)
    }

    private fun scheduleFullscreen(context: Context, duration: Long) {
        val fullscreenIntent = createFullscreenServiceIntent(context)
        val pendingIntent = PendingIntent.getService(
            context,
            REQUEST_FULLSCREEN,
            fullscreenIntent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val triggerTime = SystemClock.elapsedRealtime() + duration
        AlarmManagerCompat.setExactAndAllowWhileIdle(
            mAlarmManager,
            AlarmManager.ELAPSED_REALTIME_WAKEUP,
            triggerTime,
            pendingIntent
        )
    }

    companion object {
        private const val TAG = "ScheduledService"
        const val NAME_SHARED_PREF = "schedule"

        private const val REQUEST_FULLSCREEN = 1000
        private const val ACTION_RESET = "RESET"
        private const val ACTION_COUNT_UP = "COUNT_UP"
        private const val ACTION_FULLSCREEN = "FULLSCREEN"

        private const val ARG_DURATION = "DURATION"

        private const val KEY_STARTED_AT = "STARTED_AT"
        const val KEY_LAST_STARTED_AT = "LAST_STARTED_AT"
        const val KEY_UPDATED_AT = "UPDATED_AT"

        fun createCountUpServiceIntent(context: Context, duration: Long): Intent {
            return Intent(context, ScheduledService::class.java).apply {
                action = ACTION_COUNT_UP
                putExtra(ARG_DURATION, duration)
            }
        }

        fun createResetServiceIntent(context: Context): Intent {
            return Intent(context, ScheduledService::class.java).apply {
                action = ACTION_RESET
            }
        }

        private fun createFullscreenServiceIntent(context: Context): Intent {
            return Intent(context, ScheduledService::class.java).apply {
                action = ACTION_FULLSCREEN
            }
        }
    }
}