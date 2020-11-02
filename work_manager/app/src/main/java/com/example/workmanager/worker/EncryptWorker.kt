package com.example.workmanager.worker

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf

class EncryptWorker(context: Context, parameters: WorkerParameters) : Worker(context, parameters) {
    override fun doWork(): Result {
        val message = inputData.getString("MESSAGE") ?: return Result.failure()
        val outputData = workDataOf(
            "MESSAGE" to "Encrypted $message"
        )
        return Result.success(outputData)
    }
}