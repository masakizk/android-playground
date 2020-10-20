# Firebase Cloud Messaging
## セッティング
AndroidStudioのFirebaseツールを利用すれば、依存関係の登録とjsonファイルの取得までできる。

## トークン
メッセージを送信するために、トークンを取得する
```kotlin
FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
    if (!task.isSuccessful) {
        Log.w(TAG, "Fetching FCM registration token failed", task.exception)
        return@OnCompleteListener
    }

    // Firebase Cloud Messagingの登録トークンを取得する
    val token = task.result
})
```
## テスト通知を送信する
1. ターゲット デバイスでアプリをインストールして実行します。

1. アプリがデバイスの**バックグラウンドで動作**していることを確認します。

1. [Notifications Composer](https://console.firebase.google.com/u/0/project/_/notification)を起動を開き、[新しい通知] を選択します。

1. 通知テキストを入力します。

1. [テスト メッセージを送信] を選択します。

1. [FCM 登録トークンを追加] というラベルの付いたフィールドで、このガイドの前のセクションで取得した登録トークンを入力します。

1. [テスト] をクリックします。

## 通知を受信する
フォアグラウンドでは、通知は表示されないので`FirebaseMessagingService`を継承したクラスで処理を行う必要がある。

- FirebaseMessagingServiceを継承したクラスを作成
  ```kotlin
  class MyFirebaseMessagingService: FirebaseMessagingService()
  ```

  ```kotlin
  override fun onMessageReceived(remoteMessage: RemoteMessage) {
      super.onMessageReceived(remoteMessage)
      
      Log.d(TAG, "From: ${remoteMessage.from}")

      // データペイロードを確認
      if (remoteMessage.data.isNotEmpty()) {
          Log.d(TAG, "Message data payload: ${remoteMessage.data}")
      }

      // 通知ペイロードを確認
      remoteMessage.notification?.let {
          Log.d(TAG, "Message Notification Body: ${it.body}")
      }
  }
  ```
- Android Manifestに追加
  ```xml
  <application
      ... >
      <service
          android:name=".MyFirebaseMessagingService"
          android:exported="false">
          <intent-filter>
              <action android:name="com.google.firebase.MESSAGING_EVENT" />
          </intent-filter>
      </service>
  ```