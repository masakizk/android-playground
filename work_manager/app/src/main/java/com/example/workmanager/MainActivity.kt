package com.example.workmanager

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.work.WorkManager
import com.example.workmanager.workrequest.DataWorkRequest

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        // WorkとWorkRequestを定義し、
        // 処理をキューに登録する
        WorkManager
            .getInstance(applicationContext)
            .enqueue(DataWorkRequest.createWorkRequest())

    }
}