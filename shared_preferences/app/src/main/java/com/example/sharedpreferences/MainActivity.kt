package com.example.sharedpreferences

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.sharedpreferences.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var count: Int = 0

    private val pref: SharedPreferences
        get() {
            // 名前で識別される共有環境設定ファイル
            // return getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)

            // このアクティビティに対する共有設定ファイル
            return getPreferences(Context.MODE_PRIVATE)
        }

    companion object {
        private const val COUNT_KEY = "count"
        private const val FILE_NAME = "activity-main"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onStart() {
        super.onStart()
        binding.increamentButton.setOnClickListener {
            increment()
        }
        setCounterText()
    }

    private fun setCounterText() {
        // 値を取得
        count = pref.getInt(COUNT_KEY, 0)
        binding.counter.text = count.toString()
    }

    private fun increment() {
        val editor = pref.edit()
        with(editor) {
            putInt(COUNT_KEY, ++count)
            commit()
        }
        setCounterText()
    }
}