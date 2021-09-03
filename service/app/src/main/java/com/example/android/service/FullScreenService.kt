package com.example.android.service

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.os.SystemClock
import android.util.Log
import androidx.core.app.AlarmManagerCompat
import androidx.core.app.NotificationManagerCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Android10以降、バックグラウンドからActivityを起動するには、様々な制限がある。
 * https://www.google.com/search?q=android+service+startActivity&rlz=1C5CHFA_enJP861JP861&oq=android+service+startActivity&aqs=chrome..69i57j0i30l2.7495j0j7&sourceid=chrome&ie=UTF-8
 * よって、setFullScreenIntent により、スリープ時にのみActivityを起動できるようにし、それ以外は通常の通知として表示させることが推奨されている。
 */
class FullScreenService : Service() {
    private lateinit var mNotificationManager: NotificationManagerCompat

    override fun onCreate() {
        super.onCreate()
        mNotificationManager = NotificationManagerCompat.from(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> {
                Log.d(TAG, "onStartCommand: $ACTION_START")
                startForeground(NotificationConstants.NORMAL.ID, createNotification("start"))

                val intent = createFullscreenIntent(this)
                val pendingIntent = PendingIntent.getService(
                    this,
                    0,
                    intent,
                    PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_UPDATE_CURRENT
                )

                val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
                val trigger = SystemClock.elapsedRealtime() + 5 * 1000L
                AlarmManagerCompat.setExactAndAllowWhileIdle(
                    alarmManager,
                    AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    trigger,
                    pendingIntent
                )
                stopForeground(true)
                stopSelf(startId)
            }

            ACTION_FULLSCREEN -> {
                Log.d(TAG, "onStartCommand: $ACTION_FULLSCREEN")
                mNotificationManager.cancel(NotificationConstants.HeadUp.ID)
                mNotificationManager.notify(
                    NotificationConstants.HeadUp.ID,
                    createFullscreenNotification("DONE")
                )

                // すぐに停止すると、Activityが起動しないことがある。
                CoroutineScope(Dispatchers.Default).launch {
                    delay(1 * 1000L)
                    mNotificationManager.notify(
                        NotificationConstants.HeadUp.ID,
                        createFullscreenNotification("UPDATE")
                    )

                    delay(5 * 1000L)
                    stopSelf()
                }
            }
        }

        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    companion object {
        private const val ACTION_START = "START"
        private const val ACTION_FULLSCREEN = "FULLSCREEN"

        fun createStartIntent(context: Context): Intent {
            return Intent(context, FullScreenService::class.java).apply {
                action = ACTION_START
            }
        }

        fun createFullscreenIntent(context: Context): Intent {
            return Intent(context, FullScreenService::class.java).apply {
                action = ACTION_FULLSCREEN
            }
        }

        private const val TAG = "FullScreenService"
    }
}