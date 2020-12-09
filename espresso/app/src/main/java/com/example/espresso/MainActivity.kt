package com.example.espresso

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var mMessageText: TextView
    private lateinit var mMessageEditText: EditText
    private lateinit var mChangeMessageButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mMessageText = findViewById(R.id.text_hello_world)
        mMessageEditText = findViewById(R.id.et_message)
        mChangeMessageButton = findViewById(R.id.button_change_message)

        // ボタンが押されたらメッセージを更新
        mChangeMessageButton.setOnClickListener {
            val message = mMessageEditText.text.toString()
            mMessageText.text = if (message.isNotBlank()) message else "Espresso"
        }
    }
}