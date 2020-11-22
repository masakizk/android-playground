package com.example.daggerhilt.workmanager

import android.content.Context
import android.util.Log
import androidx.hilt.Assisted
import androidx.hilt.work.WorkerInject
import androidx.work.Worker
import androidx.work.WorkerParameters
import dagger.hilt.android.qualifiers.ApplicationContext

class GreetWorker @WorkerInject constructor(
    private val workerDependency: WorkerDependencyInterface,
    @Assisted @ApplicationContext context: Context,
    @Assisted workerParam: WorkerParameters,
): Worker(context, workerParam) {
    override fun doWork(): Result {
        Log.d(TAG, "doWork: ${workerDependency.message}")
        return Result.success()
    }
    
    companion object{
        private const val TAG = "GreetWorker"
    }
}