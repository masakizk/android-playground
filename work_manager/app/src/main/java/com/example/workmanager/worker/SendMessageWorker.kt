package com.example.workmanager.worker

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters

class SendMessageWorker(context: Context, workerParameters: WorkerParameters): Worker(context, workerParameters) {
    override fun doWork(): Result {
        val message = inputData.getString("MESSAGE") ?: return Result.failure()
        val sender = inputData.getString("SENDER") ?: return Result.failure()

        sendMessage(message, sender)
        return Result.success()
    }

    private fun sendMessage(message: String, sender: String){
        Log.d(TAG, "sendMessage: message[ $message ] from $sender")
    }
    
    companion object{
        private const val TAG = "SendMessageWorker"
    }
}