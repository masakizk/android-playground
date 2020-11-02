package com.example.workmanager.worker

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters

class SendWorker(context: Context, workerParams: WorkerParameters): Worker(context, workerParams) {
    override fun doWork(): Result {
        // なにか作業をする
        sendMessage()

        // success | fail | retry
        return Result.success()
    }

    private fun sendMessage(){
        Log.d(TAG, "sendMessage: Finished")
    }

    companion object{
        private const val TAG = "SendWorker"
    }
}