package com.example.alarmmanager

import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class AlarmReceiver : BroadcastReceiver() {
    companion object{
        private const val TAG = "AlarmReceiver"

        // レシーバーを呼び出し可能にする
        fun enableReceiver(context: Context){
            val receiver = ComponentName(context, AlarmReceiver::class.java)

            context.packageManager.setComponentEnabledSetting(
                receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP
            )
        }
    }
    
    override fun onReceive(context: Context, intent: Intent) {
        Log.d(TAG, "onReceive: Alarm is Called!")

        if (intent.action == "android.intent.action.BOOT_COMPLETED"){
            Log.d(TAG, "Device was rebooted")
            showNotification(context,"Device was rebooted")
            return
        }

        // アラームが呼ばれたら通知を出す
        showNotification(context, "Alarm Notification")
    }

    private fun showNotification(context: Context, message: String){
        val builder = NotificationCompat.Builder(context, "AlarmManagerNotification")
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentTitle(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
        val notification = builder.build()
        val notificationManager = NotificationManagerCompat.from(context)
        notificationManager.notify(R.string.app_name, notification)
    }

}