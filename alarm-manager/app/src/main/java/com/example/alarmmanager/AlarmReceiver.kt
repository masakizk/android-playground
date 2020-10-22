package com.example.alarmmanager

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class AlarmReceiver : BroadcastReceiver() {
    companion object{
        private const val TAG = "AlarmReceiver"
    }
    
    override fun onReceive(context: Context, intent: Intent?) {
        Log.d(TAG, "onReceive: Alarm is Called!")
        // アラームが呼ばれたら通知を出す
        val builder = NotificationCompat.Builder(context, "AlarmManagerNotification")
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentTitle("Alarm Notification")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
        val notification = builder.build()
        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.notify(R.string.app_name, notification)
    }

}