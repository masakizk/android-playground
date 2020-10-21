package com.example.intent

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.intent.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            goToSecondActivityButton.setOnClickListener { startSecondActivity() }
            showAppChooser.setOnClickListener { showAppChooser() }
            actionSend.setOnClickListener { sendMessage() }
        }
    }

    private fun startSecondActivity() {
        // インテントを作成
        val intent = Intent(this, SecondActivity::class.java)
        // データをセット
        val message = "Hello World"
        intent.putExtra("message", message)
        // 遷移先の画面を起動
        startActivity(intent)
    }

    private fun sendMessage() {
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, "Message")
            type = "text/plain"
        }

        // インテントを解決するアクティビティを確認
        if (sendIntent.resolveActivity(packageManager) != null) {
            startActivity(sendIntent)
        }else {
            Toast.makeText(this, "Resolve Activity was not found", Toast.LENGTH_LONG).show()
        }
    }

    // 明示的にアプリチューザーを表示する
    private fun showAppChooser() {
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, "Message")
            type = "text/plain"
        }

        // アプリチューザーダイアログを表示するインテントを作成
        val chooser = Intent.createChooser(sendIntent, "タイトル")

        if (sendIntent.resolveActivity(packageManager) != null) {
            startActivity(chooser)
        }else{
            Toast.makeText(this, "Resolve Activity was not found", Toast.LENGTH_LONG).show()
        }
    }
}