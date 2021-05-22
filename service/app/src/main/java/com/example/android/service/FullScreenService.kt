package com.example.android.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import android.os.Vibrator
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class FullScreenService : Service() {
    private lateinit var mNotificationManager: NotificationManager

    private val scope = CoroutineScope(Dispatchers.Default)

    override fun onCreate() {
        super.onCreate()
        mNotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        startForeground(NOTIFICATION_ID, createNotification("start"))
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        scope.launch {
            delay(5000)

            val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
            val wakeLock = powerManager.newWakeLock(
                PowerManager.PARTIAL_WAKE_LOCK,
                "FullScreenService::WAKE_LOCK"
            ).apply {
                acquire()
            }

            /**
             * Show fullscreen activity.
             */
//            val fullScreenIntent =
//                Intent(applicationContext, FullscreenActivity::class.java).apply {
//                    setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                }
//            val fullscreenPendingIntent = PendingIntent.getActivity(
//                applicationContext,
//                0,
//                fullScreenIntent,
//                PendingIntent.FLAG_UPDATE_CURRENT
//            )
//            fullscreenPendingIntent.send()

            val notification = createNotification("Do some work...", fullscreen = true)
            startForeground(NOTIFICATION_ID, notification)

            val mVibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            mVibrator.vibrate(longArrayOf(0, 500, 500), -1)

            wakeLock.release()

            delay(5000)
            stopSelf()
        }

        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun createNotification(message: String, fullscreen: Boolean = false): Notification {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel()
        }
        val notificationBuilder = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID).apply {
            setSmallIcon(R.drawable.ic_launcher_foreground)
            setChannelId(NOTIFICATION_CHANNEL_ID)
            setContentTitle("FULLSCREEN SERVICE")
            setContentText(message)

            priority = NotificationCompat.PRIORITY_HIGH
            setCategory(NotificationCompat.CATEGORY_ALARM)
        }

        if(fullscreen){
            Log.d(TAG, "createNotification: Create")
            /**
             * https://developer.android.com/training/notify-user/time-sensitive
             * https://developer.android.com/guide/components/activities/background-starts
             */
            val fullScreenIntent = Intent(applicationContext, FullscreenActivity::class.java)
            val fullscreenPendingIntent = PendingIntent.getActivity(
                applicationContext,
                0,
                fullScreenIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
            notificationBuilder.setFullScreenIntent(fullscreenPendingIntent, true)
        }

        return notificationBuilder.build()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            "full screen service",
            NotificationManager.IMPORTANCE_HIGH
        )
        mNotificationManager.createNotificationChannel(channel)
    }

    companion object {
        private const val NOTIFICATION_ID = 2000
        private const val NOTIFICATION_CHANNEL_ID = "FULLSCREEN_SERVICE"

        private const val TAG = "FullScreenService"

    }
}