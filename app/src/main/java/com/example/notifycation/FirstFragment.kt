package com.example.notifycation

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.NotificationCompat
import androidx.fragment.app.Fragment
import com.example.notifycation.databinding.FragmentFirstBinding

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
        binding.simpleNotification.setOnClickListener {
            val notification = createSimpleNotification()
            showNotification(notification)
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

    // NotificationManagerに対して,通知を渡すことで表示できる
    private fun showNotification(notification: Notification) {
        createNotificationChannel()
        val notificationManager =
            requireActivity().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(R.string.app_name, notification)
    }

    // Android 8.0以上で通知を配信するには、
    // NotificationChannel のインスタンスを createNotificationChannel() に渡すことにより、
    // アプリの通知チャネルをシステムに登録しておく必要があります。
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_name)
            val descriptionText = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT

            val channel = NotificationChannel(CHANNEL_ID, name, importance)
                .apply { description = descriptionText }

            // 通知チャンネルをシステムに登録
            val notificationManager =
                requireActivity().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            if (notificationManager.getNotificationChannel(CHANNEL_ID) == null) {
                notificationManager.createNotificationChannel(channel)
            }
        }
    }
}