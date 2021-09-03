package com.example.android.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

@RequiresApi(Build.VERSION_CODES.O)
fun createNormalNotificationChannel(notificationManager: NotificationManagerCompat) {
    val channel = NotificationChannel(
        NotificationConstants.NORMAL.CHANNEL_ID,
        "normal",
        NotificationManager.IMPORTANCE_DEFAULT
    ).apply {
        setSound(null, null)
    }
    notificationManager.createNotificationChannel(channel)
}

@RequiresApi(Build.VERSION_CODES.O)
fun createHeadUpNotificationChannel(notificationManager: NotificationManagerCompat) {
    val channel = NotificationChannel(
        NotificationConstants.HeadUp.CHANNEL_ID,
        "full screen service",
        NotificationManager.IMPORTANCE_HIGH
    ).apply {
        setSound(null, null)
    }
    notificationManager.createNotificationChannel(channel)
}

@JvmName("createNotification1")
fun Context.createNotification(message: String): Notification {
    return createNotification(this, message)
}

fun createNotification(context: Context, message: String): Notification {
    val notificationManager = NotificationManagerCompat.from(context)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        createNormalNotificationChannel(notificationManager)
    }

    val notificationBuilder = NotificationCompat.Builder(
        context,
        NotificationConstants.NORMAL.CHANNEL_ID
    ).apply {
        setSmallIcon(R.drawable.ic_launcher_foreground)
        setChannelId(NotificationConstants.NORMAL.CHANNEL_ID)
        setContentTitle("Normal Notification")
        setContentText(message)
        setOngoing(true)
    }

    return notificationBuilder.build()
}

@JvmName("createFullscreenNotification1")
fun Context.createFullscreenNotification(message: String): Notification {
    return createFullscreenNotification(this, message)
}

fun createFullscreenNotification(context: Context, message: String): Notification {
    val notificationManager = NotificationManagerCompat.from(context)
    // フルスクリーン表示したいときは優先度を最大にする。
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        createHeadUpNotificationChannel(notificationManager)
    }

    val fullScreenIntent = Intent(context, FullscreenActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_NO_USER_ACTION
    }
    val fullscreenPendingIntent = PendingIntent.getActivity(
        context,
        NotificationConstants.HeadUp.ID,
        fullScreenIntent,
        PendingIntent.FLAG_UPDATE_CURRENT
    )

    return NotificationCompat.Builder(context, NotificationConstants.HeadUp.CHANNEL_ID)
        .apply {
            setSmallIcon(R.drawable.ic_launcher_foreground)
            setContentTitle("FULLSCREEN NOTIFICATION")
            setContentText(message)
            setAutoCancel(true) // タップして開いたら終了

            priority = NotificationCompat.PRIORITY_HIGH
            setCategory(NotificationCompat.CATEGORY_ALARM)
            setChannelId(NotificationConstants.HeadUp.CHANNEL_ID)

            setFullScreenIntent(fullscreenPendingIntent, true)
        }
        .build()
}

object NotificationConstants {
    object NORMAL {
        const val ID = 1000
        const val CHANNEL_ID = "NORMAL_NOTIFICATION"
    }

    object HeadUp {
        const val ID = 2000
        const val CHANNEL_ID = "HEAD_UP_SERVICE"
    }
}