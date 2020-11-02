package com.example.workmanager.workrequest

import androidx.work.*
import com.example.workmanager.worker.SendWorker
import java.util.concurrent.TimeUnit

object ScheduleWorkRequest {
    // WorkRequestを定義
    fun createSendRequest(): WorkRequest {
        return OneTimeWorkRequestBuilder<SendWorker>().build()
    }

    /*
        処理のスケジュール設定
        一回限りの処理の設定
     */
    fun createOneTimeWork(): OneTimeWorkRequest {
        // 一回限りの処理のスケジュール
        // 追加の設定が不要な場合は、fromを使用
        return OneTimeWorkRequest.from(SendWorker::class.java)
    }

    /*
        処理のスケジュール設定
        定期的な処理の設定
     */
    fun createPeriodicWorkRequest(): PeriodicWorkRequest {
        // 1時間に一回のスケジュール
        // 最小のインターバルは15分
        return PeriodicWorkRequestBuilder<SendWorker>(
            repeatInterval = 1,
            repeatIntervalTimeUnit = TimeUnit.HOURS
        ).build()
    }

    fun createFlexiblePeriodicWork(): WorkRequest {
        //　1時間に一回のスケジュール
        // 1時間ぴったりではなく、15分の期間内に実行される
        return PeriodicWorkRequestBuilder<SendWorker>(
            repeatInterval = 1,
            repeatIntervalTimeUnit = TimeUnit.HOURS,
            flexTimeInterval = 15,
            flexTimeIntervalUnit = TimeUnit.MINUTES
        ).build()
    }

    /*
        処理の遅延
     */
    fun createDelayedWorkRequest(): WorkRequest {
        // 最小限の初期遅延を設定
        // 処理がキューに登録されてから、10分以上立ったらタスクを実行
        return OneTimeWorkRequestBuilder<SendWorker>()
            .setInitialDelay(10, TimeUnit.MINUTES)
            .build()
    }

    /*
        再試行
        処理を再試行する場合は、ワーカからResult.retry()を返す。
        その後、バックオフ遅延とバックオフポリシーに従って処理のスケジュールが変更される。

        バックオフ遅延:    再試行するまでに待機する最小時間
        バックオフポリシー: 以降の再試行のバックオフ遅延がどの程度増加するかを定義(LINEAR | EXPONENTIAL)
     */
    fun createWorkRequestWithBackoffPolicy(): WorkRequest {
        // 最小バックオフ遅延: 10秒
        // バックオフポリシー: LINEAR
        // -> 再試行するごとに再試行間隔が10秒ずつ増加する
        return OneTimeWorkRequestBuilder<SendWorker>()
            .setBackoffCriteria(
                BackoffPolicy.LINEAR,
                OneTimeWorkRequest.MIN_BACKOFF_MILLIS,
                TimeUnit.MILLISECONDS
            )
            .build()
    }
}