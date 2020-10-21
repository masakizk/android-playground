package com.example.intent

import android.app.Activity
import android.os.Bundle
import com.example.intent.databinding.ActivitySecondBinding

class SecondActivity : Activity() {
    private lateinit var binding: ActivitySecondBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySecondBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setActivity()
    }

    private fun setActivity(){
        val message = intent.getStringExtra("message")
        binding.message.text = message
    }
}