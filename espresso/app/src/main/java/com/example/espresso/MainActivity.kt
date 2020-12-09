package com.example.espresso

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.button_change_message)
            .setOnClickListener {
                // ボタンを押されたら、テキストを更新
                findViewById<TextView>(R.id.text_hello_world).text = "Espresso"
            }
    }
}