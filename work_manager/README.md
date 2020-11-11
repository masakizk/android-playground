# WorkManager
アプリが終了した場合やデバイスが再起動した場合でも実行される、

延期可能な非同期タスクのスケジュールを簡単に設定するための API

## 準備
```kotlin
def work_version = "2.4.0"
// Kotlin + coroutines
implementation "androidx.work:work-runtime-ktx:$work_version"
```

## 流れ 
1. ### Workerを作成  
    WorkManagerが実行するタスク
    ```kotlin
    class SendWorker(context: Context, workerParams: WorkerParameters)
      : Worker(context, workerParams) {

        override fun doWork(): Result {
            // なにか処理をする
            sendMessage()

            // 結果を返す
            // success | fail | retry
            return Result.success()
        }
        ...
    }
    ```
2. ### WorkRequestを作成
    ```kotlin
    val workRequest = OneTimeWorkRequestBuilder<SendWorker>().build()
    ```
3. ### システムにWorkRequestを送信する
    処理をキューに登録する

    ```kotlin
    WorkManager
        .getInstance(applicationContext)
        .enqueue(DataWorkRequest.createWorkRequest())
    ```

## Workerデータをやり取りする
入力データKey-Valueのペアとして,Dataオブジェクトに保存される。
```kotlin
// WorkerRequest
val inputData: Data = workDataOf(
    "SENDER" to "Alice",
    "MESSAGE" to "Hello"
)

OneTimeWorkRequestBuilder<EncryptMessageWorker>()
    .setInputData(inputData)
    .build()
```
```kotlin
// 取得
val message = inputData.getString("MESSAGE") ?: return Result.failure()

// 出力
val outputData = workDataOf(
  "MESSAGE" to "Encrypted $message"
)

return Result.success(outputData)
```

データの取得

Workerの`inputData`から取得できる

```kotlin
class SendMessageWorker(context: Context, workerParameters: WorkerParameters): Worker(context, workerParameters) {
    override fun doWork(): Result {
        val message = inputData.getString("MESSAGE") ?: return Result.failure()
        val sender = inputData.getString("SENDER") ?: return Result.failure()

        sendMessage(message, sender)
        return Result.success()
    }
		...
}
```
## 長時間ワーカー
- 処理の実行中にプロセスを可能な限り維持する必要があることを OS に通知できる
- すぐに実行される
- ### Coroutine Workerを実装
  ```kotlin
  class LongTimeWorker(context: Context, parameters: WorkerParameters) :
      CoroutineWorker(context, parameters) {
      
      override suspend fun doWork(): Result {
          // 通知をフォアグラウンドタスクとして呼び出す
          setForeground(createForegroundInfo("Started"))

          // 長時間タスクを実行
          longTimeTask()

          return Result.success()
      }
  ```

- ### ForgroundInfoを作成
  ```kotlin
  private fun createForegroundInfo(progress: String): ForegroundInfo {
      // タスク実行中に表示する通知を作成する
      val notification = createNotification(progress)
      return ForegroundInfo(NOTIFICATION_ID, notification)
  }
  ```
## WorkRequest
Workerをどのように動作させるかをシステムに伝えるためのリクエスト
- ### スケジュール
  - #### OneTimeWorkRequest
    一回だけ実行
    ```kotlin
    OneTimeWorkRequestBuilder<SendWorker>().build()
    ```
  - #### PeriodicWorkRequest
    定期的に実行
    ```kotlin
    // 一時間に一回実行(最小15分のインターバルを指定可能)
    PeriodicWorkRequestBuilder<SendWorker>(
        repeatInterval = 1,
        repeatIntervalTimeUnit = TimeUnit.HOURS
    ).build()
    ```
- ### 実行時の制約
  - #### ネットワークに関する制約
    ```kotlin
    val constrains = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
    ```
    ```
    CONNECTED: ネットワーク接続を要求
    NOT_REQUIRED:　ネットワーク接続は必須でない
    UNMETERED: ネットワークの通信に制限がない(WiFi)
    METERED: ネットワークの通信に制約がある
    NOT_ROAMING: 契約している事業者の回線である
    ```
  - #### バッテリーに関する制約
    ```kotlin
    // 電池残量低下モードになっていないか
    val constrains = Constraints.Builder()
            .setRequiresBatteryNotLow(true)
            .build()
    ```
  - #### 充電状況に関する制約
    ```kotlin
    // 充電中であるか
    val constrains = Constraints.Builder()
            .setRequiresCharging(true)
            .build()
    ```
  - #### 保存領域に関する制約
    ```kotlin
    // 保存領域が少なくないか
    val constrains = Constraints.Builder()
            .setRequiresStorageNotLow(true)
            .build()
    ```