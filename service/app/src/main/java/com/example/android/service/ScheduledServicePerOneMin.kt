package com.example.android.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.os.SystemClock
import android.util.Log
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.edit
import kotlinx.coroutines.*
import java.util.*

/**
 * 指定された秒数カウントアップを続け、
 * それが終了しとき、スリープを解除しフルスクリーンのアクティビティを表示するサービス
 */
class ScheduledServicePerOneMin : Service() {

    private lateinit var mAlarmManager: AlarmManager
    private lateinit var mNotificationManager: NotificationManagerCompat
    private val mSharedPreferences
        get() = applicationContext.getSharedPreferences(
            NAME_SHARED_PREF,
            Context.MODE_PRIVATE
        )

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
            ACTION_START -> {
                Log.d(TAG, "onStartCommand: Start")

                val duration = intent.getLongExtra(ARG_DURATION, -1L)
                mSharedPreferences.edit {
                    putLong(ARG_DURATION, duration)
                    putLong(KEY_STARTED_AT, Calendar.getInstance().timeInMillis)
                    putLong(KEY_LAST_STARTED_AT, Calendar.getInstance().timeInMillis)
                }

                val intent = createUpdateServiceIntent(applicationContext)
                startService(intent)

                return START_NOT_STICKY
            }

            ACTION_UPDATE -> {
                Log.d(TAG, "onStartCommand: UPDATE")
                val notification = createNotification(ACTION_UPDATE)
                startForeground(NOTIFICATION_ID, notification)

                val duration = mSharedPreferences.getLong(ARG_DURATION, -1L)
                if (duration == -1L) return START_NOT_STICKY

                val startedAt = mSharedPreferences.getLong(KEY_STARTED_AT, -1L)
                if (startedAt == -1L) return START_NOT_STICKY

                CoroutineScope(Dispatchers.IO).launch {
                    delay(10 * 1000L)

                    val now = Calendar.getInstance().timeInMillis
                    val elapsed = now - startedAt
                    mSharedPreferences.edit { putLong(KEY_UPDATED_AT, now) }

                    val updatePendingIntent = createUpdateServiceIntent(applicationContext)
                    val alarmManager =
                        applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                    val remainingTime = duration - elapsed

                    // タイマー終了
                    if (remainingTime <= 0) {
                        Log.d(TAG, "onStartCommand: DONE")

                        startService(createDoneServiceIntent(this@ScheduledServicePerOneMin))

                        // AlarmManagerをキャンセル
                        val pendingIntent = PendingIntent.getService(
                            applicationContext,
                            0,
                            updatePendingIntent,
                            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_NO_CREATE
                        )
                        if (pendingIntent != null) {
                            alarmManager.cancel(pendingIntent)
                            pendingIntent.cancel()
                        }

                        mSharedPreferences.edit {
                            remove(KEY_STARTED_AT)
                            remove(ARG_DURATION)
                        }

                        return@launch
                    }

                    // 次の分数が変化するときに、呼び出す。
                    Log.d(TAG, "onStartCommand: $remainingTime")
                    val pendingIntent = PendingIntent.getService(
                        applicationContext,
                        REQUEST_CODE_UPCOMING,
                        updatePendingIntent,
                        PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_UPDATE_CURRENT
                    )
                    val nextMinuteChange = remainingTime % (60 * 1000)
                    val triggerTime = SystemClock.elapsedRealtime() + nextMinuteChange
                    schedulePendingIntent(alarmManager, triggerTime, pendingIntent)

                    val notification = createNotification("${elapsed / (1000 * 60)}min")
                    mNotificationManager.cancel(NOTIFICATION_ID) // 優先度が低いため、キャンセルしないと更新されない
                    startForeground(NOTIFICATION_ID, notification)
                }.invokeOnCompletion {
                    stopSelf()
                    Log.d(TAG, "invokeOnCompletion: ${it}")
                }

                return START_NOT_STICKY
            }

            ACTION_DONE -> {
                val notification = createFullscreenNotification("DONE")
                mNotificationManager.cancel(NotificationConstants.HeadUp.ID)
                mNotificationManager.notify(NotificationConstants.HeadUp.ID, notification)
                CoroutineScope(Dispatchers.Default).launch {
                    delay(10 * 1000L)
                    stopSelf(startId)
                }
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

    private fun schedulePendingIntent(
        alarmManager: AlarmManager,
        triggerTime: Long,
        pendingIntent: PendingIntent?
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.ELAPSED_REALTIME_WAKEUP,
                triggerTime,
                pendingIntent
            )
        } else {
            alarmManager.setExact(
                AlarmManager.ELAPSED_REALTIME_WAKEUP,
                triggerTime,
                pendingIntent
            )
        }
    }

    companion object {
        private const val TAG = "ScheduledServicePerOneM"
        const val NAME_SHARED_PREF = "schedule"
        private const val ACTION_RESET = "RESET"
        private const val ACTION_START = "START"
        private const val ACTION_UPDATE = "UPDATE"
        private const val ACTION_DONE = "DONE"

        private const val ARG_DURATION = "DURATION"

        private const val KEY_STARTED_AT = "STARTED_AT"
        const val KEY_LAST_STARTED_AT = "LAST_STARTED_AT"
        const val KEY_UPDATED_AT = "UPDATED_AT"

        private const val NOTIFICATION_ID = 4000
        private const val NOTIFICATION_CHANNEL_ID = "SCHEDULED_SERVICE"

        private const val REQUEST_CODE_UPCOMING = 0

        fun createStartServiceIntent(context: Context, duration: Long): Intent {
            return Intent(context, ScheduledServicePerOneMin::class.java).apply {
                action = ACTION_START
                putExtra(ARG_DURATION, duration)
            }
        }

        fun createDoneServiceIntent(context: Context): Intent {
            return Intent(context, ScheduledServicePerOneMin::class.java).apply {
                action = ACTION_DONE
            }
        }

        fun createResetServiceIntent(context: Context): Intent {
            return Intent(context, ScheduledServicePerOneMin::class.java).apply {
                action = ACTION_RESET
            }
        }

        private fun createUpdateServiceIntent(context: Context): Intent {
            return Intent(context, ScheduledServicePerOneMin::class.java).apply {
                action = ACTION_UPDATE
            }
        }
    }
}