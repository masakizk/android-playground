package com.example.notifycation

import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat.startActivity

object MyNotificationChannel {
    private const val TAG = "MyNotificationChannel"

    // Android 8.0以上で通知を配信するには、
    // NotificationChannel のインスタンスを createNotificationChannel() に渡すことにより、
    // アプリの通知チャネルをシステムに登録しておく必要があります。
    fun createNotificationChannel(name: String, description: String, channelId: String, context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH

            val channel = NotificationChannel(channelId, name, importance).apply {
                this.description = description
            }

            // 通知チャンネルをシステムに登録
            val notificationManager = NotificationManagerCompat.from(context)

            if (notificationManager.getNotificationChannel(channelId) == null) {
                notificationManager.createNotificationChannel(channel)
            }
        }
    }

    fun showNotificationChanelSetting(activity: Activity){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val intent = Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS)
            intent.putExtra(Settings.EXTRA_APP_PACKAGE, activity.packageName)
            intent.putExtra(Settings.EXTRA_CHANNEL_ID, Notifications.CHANNEL_ID)
            startActivity(activity, intent, null)
        }else{
            Log.d(TAG, "showNotificationChanelSetting: This device is lower than Android:Oreo")
        }
    }
}