package com.example.notifycation

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.notifycation.databinding.FragmentFirstBinding
import kotlinx.coroutines.launch

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {
    private lateinit var binding: FragmentFirstBinding
    private val CHANNEL_ID = "simple-notification-channel"
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.apply {
            simpleNotification.setOnClickListener {
                val notification = createSimpleNotification()
                showNotification(notification)
            }

            progressNotification.setOnClickListener {
                lifecycleScope.launch {
                    (1..100).forEach { i ->
                        if(i % 20 != 0) return@forEach
                        // 頻繁に更新するとアプリに負荷をかけてしまう
                        val notification = createProgressNotification(i)
                        showNotification(notification)
                        Thread.sleep(1000L)
                    }
                }
            }

            indeterminateProgressNotification.setOnClickListener {
                val notification = createActionButtonNotification()
                showNotification(notification)
            }

            expandablePictureNotification.setOnClickListener {
                val notification = createExpandablePictureNotification()
                showNotification(notification)
            }

            expandableTextNotification.setOnClickListener {
                val notification = createExpandableTextNotification()
                showNotification(notification)
            }
        }
    }

    private fun createSimpleNotification(): Notification {
        val builder = NotificationCompat.Builder(requireContext(), CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notifications_24px)
            .setContentTitle("Notification Title")
            .setContentText("Notification Content Text.\nHello from Fragment")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        return builder.build()
    }

    private fun createProgressNotification(progressCurrent: Int): Notification {
        val PROGRESS_MAX = 100

        val builder = NotificationCompat.Builder(requireContext(), CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notifications_24px)
            .setContentTitle("Notification Title")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        if (progressCurrent == 100) {
            // インジケーターを隠すときは setProgress(0, 0, false) を呼び出す
            builder.setProgress(0, 0, false)
        } else {
            builder.setProgress(PROGRESS_MAX, progressCurrent, false)
        }

        return builder.build()
    }

    private fun createIndeterminateProgressNotification(): Notification {
        val builder = NotificationCompat.Builder(requireContext(), CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notifications_24px)
            .setContentTitle("Notification Title")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setProgress(0, 0, true)

        return builder.build()
    }

    private fun createActionButtonNotification(): Notification {
        val builder = NotificationCompat.Builder(requireContext(), CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notifications_24px)
            .setContentTitle("Notification Title")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            // TODO: Add intent
            .addAction(R.drawable.ic_notifications_24px, getString(R.string.action), null)

        return builder.build()
    }

    // 折りたたみ可能な通知
    private fun createExpandablePictureNotification(): Notification {
        val bmp = BitmapFactory.decodeResource(resources, R.drawable.sample)
        val style = NotificationCompat.BigPictureStyle()
            .bigPicture(bmp)
            .bigLargeIcon(null)

        val builder = NotificationCompat.Builder(requireContext(), CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notifications_24px)
            .setContentTitle("Notification Title")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            // アイコンを配置する
            .setLargeIcon(bmp)
            .setStyle(style)

        return builder.build()
    }

    private fun createExpandableTextNotification(): Notification {
        val style = NotificationCompat.BigTextStyle()
            .bigText("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.")

        val builder = NotificationCompat.Builder(requireContext(), CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notifications_24px)
            .setContentTitle("Notification Title")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setStyle(style)

        return builder.build()
    }

    // NotificationManagerに対して,通知を渡すことで表示できる
    private fun showNotification(notification: Notification) {
        createNotificationChannel()
        val notificationManager = NotificationManagerCompat.from(requireContext())
        notificationManager.notify(R.string.app_name, notification)

    }

    // Android 8.0以上で通知を配信するには、
    // NotificationChannel のインスタンスを createNotificationChannel() に渡すことにより、
    // アプリの通知チャネルをシステムに登録しておく必要があります。
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_name)
            val descriptionText = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_HIGH

            val channel = NotificationChannel(CHANNEL_ID, name, importance)
                .apply { description = descriptionText }

            // 通知チャンネルをシステムに登録
            val notificationManager = NotificationManagerCompat.from(requireContext())

            if (notificationManager.getNotificationChannel(CHANNEL_ID) == null) {
                notificationManager.createNotificationChannel(channel)
            }
        }
    }
}