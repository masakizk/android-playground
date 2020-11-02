package com.example.workmanager.worker

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters

class SendMessagesWorker(context: Context, workerParameters: WorkerParameters): Worker(context, workerParameters)  {
    override fun doWork(): Result {
        val messages = inputData.getStringArray("MESSAGE") ?: return Result.failure()
        messages.forEach { sendMessage(it) }
        return Result.success()
    }

    private fun sendMessage(message: String){
        Log.d(TAG, "sendMessage: message[ $message ]")
    }

    companion object{
        private const val TAG = "SendMessagesWorker"
    }
}