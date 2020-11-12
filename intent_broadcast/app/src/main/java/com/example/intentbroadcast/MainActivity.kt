package com.example.intentbroadcast

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.intentbroadcast.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            explicitIntent.setOnClickListener {
                val intent = Intent(applicationContext, MyBroadcastReceiver::class.java)
                sendBroadcast(intent)
            }

            sendMessage.setOnClickListener {
                val intent = Intent(applicationContext, MyBroadcastReceiver::class.java)
                intent.action = Intent.ACTION_SEND
                intent.putExtra(MyBroadcastReceiver.MESSAGE_KEY, "Hello World")
                sendBroadcast(intent)
            }

            apple.setOnClickListener {
                val intent = Intent(applicationContext, MyBroadcastReceiver::class.java)
                intent.action = MyBroadcastReceiver.INTENT_APPLE
                sendBroadcast(intent)
            }
        }
    }
}