package com.example.notifycation

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.example.notifycation.databinding.ActivityMessageBinding

class MessageActivity : Activity() {
    private lateinit var binding: ActivityMessageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMessageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setMessage()
    }

    private fun setMessage(){
        val message = intent.getStringExtra(Intent.ACTION_SEND)
        binding.messageText.text = message
    }
}