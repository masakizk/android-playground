package com.example.notifycation

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.notifycation.databinding.FragmentFirstBinding
import kotlinx.coroutines.launch
import kotlin.random.Random

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {
    private lateinit var binding: FragmentFirstBinding
    private lateinit var notifications: Notifications

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFirstBinding.inflate(inflater, container, false)
        notifications = Notifications(requireContext(), getString(R.string.channel_name), getString(R.string.channel_description))
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.apply {
            notificationChannel.setOnClickListener {
                MyNotificationChannel.showNotificationChanelSetting(requireActivity())
            }

            simpleNotification.setOnClickListener {
                val notification = notifications.simpleNotification()
                notifications.showNotification(notification)
            }

            progressNotification.setOnClickListener {
                lifecycleScope.launch {
                    (1..100).forEach { i ->
                        if (i % 20 != 0) return@forEach
                        // 頻繁に更新するとアプリに負荷をかけてしまう
                        val notification = notifications.progressNotification(i)
                        notifications.showNotification(notification)
                        Thread.sleep(1000L)
                    }
                }
            }

            indeterminateProgressNotification.setOnClickListener {
                val notification = notifications.actionButtonNotification()
                notifications.showNotification(notification)
            }

            expandablePictureNotification.setOnClickListener {
                val bmp = BitmapFactory.decodeResource(resources, R.drawable.sample)
                val notification = notifications.expandablePictureNotification(bmp)
                notifications.showNotification(notification)
            }

            expandableTextNotification.setOnClickListener {
                val notification = notifications.expandableTextNotification()
                notifications.showNotification(notification)
            }

            groupNotification.setOnClickListener {
                val messages = (1..5).map { i ->
                    val message = "This is notification $i"
                    val notification = notifications.groupNotification(message)
                    notifications.showNotification(notification, i * Random.nextInt())
                    message
                }
                val summary = notifications.summaryNotification(messages)
                notifications.showNotification(summary, Random.nextInt())
            }

            actionNotification.setOnClickListener {
                val notification = notifications.actionButtonNotification()
                notifications.showNotification(notification)
            }

            contentNotification.setOnClickListener {
                val notification = notifications.contentIntentNotification()
                notifications.showNotification(notification)
            }
        }
    }
}