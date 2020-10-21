package com.example.intent

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.intent.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.goToSecondActivityButton.setOnClickListener {
            startSecondActivity()
        }
    }

    private fun startSecondActivity(){
        // インテントを作成
        val intent = Intent(this, SecondActivity::class.java)
        // データをセット
        val message = "Hello World"
        intent.putExtra("message", message)
        // 遷移先の画面を起動
        startActivity(intent)
    }
}