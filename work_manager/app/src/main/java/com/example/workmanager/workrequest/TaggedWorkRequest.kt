package com.example.workmanager.workrequest

import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkRequest
import com.example.workmanager.worker.SendWorker

/*
    処理へのタグ付け
*/

object TaggedWorkRequest {
    /*
        すべてのWorkRequestにはタグ付けがされている。
        タグ付けをすることで、処理のキャンセルや進行状況を確認できる。

        タグを設定することで、処理をグループ化できる。
        タグを持つすべてのWorkRequestをキャンセルしたり、 (WorkManager.cancelAllWorkByTag(String))
        現在の処理の状態を表すWorkInfoのリストを取得できる。(WorkManager.getWorkInfosByTag(String))
     */

    fun createTaggedWorkRequest(): WorkRequest {
        return OneTimeWorkRequestBuilder<SendWorker>()
            .addTag("important")
            .build()
    }
}