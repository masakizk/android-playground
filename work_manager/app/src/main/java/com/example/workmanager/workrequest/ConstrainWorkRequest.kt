package com.example.workmanager.workrequest

import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkRequest
import com.example.workmanager.worker.SendWorker

object ConstrainWorkRequest {
    /*
        処理の制約
     */
    fun createNetworkConstrainWork(): WorkRequest {
        /*
            ネットワークに関連する制約
            CONNECTED: ネットワーク接続を要求
            NOT_REQUIRED:　ネットワーク接続は必須でない
            UNMETERED: ネットワークの通信に制限がない(WiFi)
            METERED: ネットワークの通信に制約がある
            NOT_ROAMING: 契約している事業者の回線である
         */

        val constrains = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresBatteryNotLow(true) // 電池残量低下モードになっていない
            .setRequiresCharging(true) // 充電中
            .setRequiresStorageNotLow(true) // 保存領域が少なくない
            .build()

        return OneTimeWorkRequestBuilder<SendWorker>()
            .setConstraints(constrains)
            .build()
    }
}