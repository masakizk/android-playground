package com.example.android.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.os.SystemClock
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
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

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        mAlarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        mNotificationManager = NotificationManagerCompat.from(this)
        startForeground(NOTIFICATION_ID, createNotification("starting"))
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_COUNT_UP -> {
                val argDuration = intent.getLongExtra(ARG_DURATION, -1L)
                val savedDuration = mSharedPreferences.getLong(ARG_DURATION, -1L)
                if (argDuration == -1L && savedDuration == -1L) return START_NOT_STICKY
                val duration = if (argDuration != -1L) argDuration else savedDuration
                mSharedPreferences.edit { putLong(ARG_DURATION, duration) }
                scheduleFullscreen(applicationContext, duration)

                // 1秒おきに通知を更新する
                CoroutineScope(Dispatchers.Default).launch {
                    startTimer(duration)
                    stopSelf()
                }

                return START_STICKY
            }

            ACTION_FULLSCREEN -> {
                startActivity(this)
                return START_NOT_STICKY
            }

            ACTION_RESET -> {
                mSharedPreferences.edit {
                    remove(KEY_STARTED_AT)
                    remove(ARG_DURATION)
                }
                return START_NOT_STICKY
            }
        }
        return START_NOT_STICKY
    }

    private fun startActivity(context: Context) {
        val activityIntent = Intent(context, FullscreenActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            activityIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        kotlin.runCatching {
            // 起動
            pendingIntent.send()
        }.onFailure {
            Log.e(TAG, "startActivity: failed in launching activity", it)
        }
    }

    private suspend fun startTimer(duration: Long) {
        while (true) {
            val now = Calendar.getInstance().timeInMillis
            val elapsed = now - startedAt
            if (elapsed > duration) break

            val notification = createNotification("${elapsed / 1000}")
            mNotificationManager.cancel(NOTIFICATION_ID) // 優先度が低いため、キャンセルしないと更新されない
            startForeground(NOTIFICATION_ID, notification)

            mSharedPreferences.edit(commit = true) { putLong(KEY_UPDATED_AT, now) }
            delay(1000)
        }
    }

    private fun createNotification(message: String): Notification {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel()
        }

        val notificationBuilder = NotificationCompat.Builder(
            this,
            NOTIFICATION_CHANNEL_ID
        ).apply {
            setSmallIcon(R.drawable.ic_launcher_foreground)
            setChannelId(NOTIFICATION_CHANNEL_ID)
            setContentTitle("SCHEDULED SERVICE")
            setContentText(message)
            setOngoing(true)
        }

        return notificationBuilder.build()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            "main activity service",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        mNotificationManager.createNotificationChannel(channel)
    }

    private fun scheduleFullscreen(context: Context, duration: Long) {
        val fullscreenIntent = createFullscreenServiceIntent(context)
        val pendingIntent = PendingIntent.getService(
            context,
            1000,
            fullscreenIntent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val triggerTime = SystemClock.elapsedRealtime() + duration
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mAlarmManager.setExactAndAllowWhileIdle(
                AlarmManager.ELAPSED_REALTIME_WAKEUP,
                triggerTime,
                pendingIntent
            )
        } else {
            mAlarmManager.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerTime, pendingIntent)
        }
    }

    companion object {
        private const val TAG = "ScheduledService"
        const val NAME_SHARED_PREF = "schedule"
        private const val ACTION_RESET = "RESET"
        private const val ACTION_COUNT_UP = "COUNT_UP"
        private const val ACTION_FULLSCREEN = "FULLSCREEN"

        private const val ARG_DURATION = "DURATION"

        private const val KEY_STARTED_AT = "STARTED_AT"
        const val KEY_LAST_STARTED_AT = "LAST_STARTED_AT"
        const val KEY_UPDATED_AT = "UPDATED_AT"

        private const val NOTIFICATION_ID = 4000
        private const val NOTIFICATION_CHANNEL_ID = "SCHEDULED_SERVICE"

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