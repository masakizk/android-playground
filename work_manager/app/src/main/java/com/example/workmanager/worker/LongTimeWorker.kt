package com.example.workmanager.worker

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.example.workmanager.R
import kotlinx.coroutines.delay

// すぐに実行されるWorkerを作成するには
// CoroutineWorkerをオーバーライドする
class LongTimeWorker(context: Context, parameters: WorkerParameters) :
    CoroutineWorker(context, parameters) {

    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    override suspend fun doWork(): Result {
        // setForeground
        // WorkManagerにすぐに実行する必要があることを伝える
        setForeground(createForegroundInfo("Started"))

        // 長時間タスクを実行
        longTimeTask()

        return Result.success()
    }

    // タスク実行中に表示される通知を更新するために利用される
    private fun createForegroundInfo(progress: String): ForegroundInfo {
        val notification = createNotification(progress)
        return ForegroundInfo(NOTIFICATION_ID, notification)
    }

    private suspend fun longTimeTask() {
        delay(10 * 1000L)
        setForeground(createForegroundInfo("Finished"))
    }

    private fun createNotification(progress: String): Notification {
        val id = applicationContext.getString(R.string.notification_channel_id)
        val title = "LongTimeWorker"
        // Workerをキャンセルするためのインテント
        val cancelIntent =
            WorkManager
                .getInstance(applicationContext)
                .createCancelPendingIntent(getId())

        val builder = NotificationCompat.Builder(applicationContext, id)
            .setContentTitle(title)
            .setTicker(title)
            .setContentText(progress)
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setOngoing(true)
            .addAction(R.drawable.ic_delete_24px, "Cancel", cancelIntent)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = createChannel()
            builder.setChannelId(channel.id)
        }

        return builder.build()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createChannel(): NotificationChannel {
        val id = applicationContext.getString(R.string.notification_channel_id)
        val name = applicationContext.getString(R.string.channel_name)
        return NotificationChannel(id, name, NotificationManager.IMPORTANCE_HIGH).also { channel ->
            notificationManager.createNotificationChannel(channel)
        }
    }

    companion object {
        private const val NOTIFICATION_ID = 10001
        private const val TAG = "LongTimeWorker"
    }
}