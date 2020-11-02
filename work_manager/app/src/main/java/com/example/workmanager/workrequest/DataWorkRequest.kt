package com.example.workmanager.workrequest

import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkRequest
import androidx.work.workDataOf
import com.example.workmanager.worker.SendMessageWorker

object DataWorkRequest {
    /*
       データを入力
    */
    fun createWorkRequest(): WorkRequest {

        // 入力データKey-Valueのペアとして,Dataオブジェクトに保存される。
        val inputData: Data = workDataOf(
            "SENDER" to "Alice",
            "MESSAGE" to "Hello"
        )

        return OneTimeWorkRequestBuilder<SendMessageWorker>()
            .setInputData(inputData)
            .build()
    }
}