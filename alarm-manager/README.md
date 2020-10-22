# Alarm Manager
- 設定した時間、または周期的に処理を実行できる
- デバイスがスリープ状態、アプリが起動していなくても実行できる

## 用意
- ### AlarmManager
  ```kotlin
  private lateinit var alarmMgr: AlarmManager
  private lateinit var alarmIntent: PendingIntent

  ...

  alarmMgr = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
  // アラームで呼び出されるインテントを指定
  alarmIntent = Intent(context, AlarmReceiver::class.java).let { intent ->
      PendingIntent.getBroadcast(context, 0, intent, 0)
  }
  ```
- ### Broadcast Receiver
  `AlarmManager`によって呼び出される、なにかの処理をするところ
  ```kotlin
  class AlarmReceiver : BroadcastReceiver()
  ```

  ```kotlin
  override fun onReceive(context: Context, intent: Intent?) {
    Log.d(TAG, "onReceive: Alarm is Called!")
    /* アラームが呼ばときになにか処理をする */
  }
  ```
- ### Android Manifest
    レシーバを登録
    ```xml
    <application...>
        <receiver android:name=".AlarmReceiver"/>
    </application>
    ```
## アラームを設定する
**アラームタイプ**, **アラームの時間**, **ペンディングインテント**を指定することでアラームを作成できる
```kotlin
alarmMgr.set(
    AlarmManager.ELAPSED_REALTIME,
    SystemClock.elapsedRealtime() + 10 * 1000,
    alarmIntent
)
```

## アラームタイプ
- ### XX_WAKEUP
  スリープ状態でもスリープを解除して実行する  
  WAKEUPがついていないアラームタイプは、スリープが解除されたら実行する。  
  どちらの場合でも、アプリが実行されていなくても実行される

- ### ELAPSED_REALTIME(_WAKEUP)
  デバイス起動してから指定した時間が経過したあと,ペンディングインテントを開始
  ```kotlin
  alarmMgr.set(
      AlarmManager.ELAPSED_REALTIME,
      SystemClock.elapsedRealtime() + 10 * 1000,
      alarmIntent
  )
  ```
- ### RTC
  指定された時間にペンディングインテントを開始
  ```kotlin
  // 現在の時刻から10秒後を指定
  val calendar: Calendar = Calendar.getInstance().apply {
      timeInMillis = System.currentTimeMillis() + 10*1000
  }

  alarmMgr.set(
      AlarmManager.RTC,
      calendar.timeInMillis,
      alarmIntent
  )
  ```

## デバイス再起動時にアラームを開始
デフォルトではデバイスをシャットダウンすると、**すべてのアラームがキャンセルされる**
再起動時に反復アラームを再開するように設定できる。
- ### 権限付与
  `RECEIVE_BOOT_COMPLETED`権限を設定すると、システムが起動後にブロードキャストされる`ACTION_BOOT_COMPLETED`を受信することができる。
  ```xml
  <uses-permission android:name="android.permission.RECEIVE_BOOT_COMPLETED"/>
  ```
- ### Broadcast Receiver
  ```kotlin
  override fun onReceive(context: Context, intent: Intent) {
      if (intent.action == "android.intent.action.BOOT_COMPLETED") {
          // 再起動時に呼ばれる
      }
  }
  ```
- ### インテントフィルタ
  `android:enabled="false"`: アプリで明示的にレシーバーを有効にしないと、呼び出せない  
  => 起動レシーバーが不必要に呼び出されるのを防ぐ
    ```xml
    <receiver 
        android:name=".SampleBootReceiver"
        android:enabled="false">
        <intent-filter>
            <action android:name="android.intent.action.BOOT_COMPLETED" />
        </intent-filter>
    </receiver>
    ```
- ### レシーバーを有効にする
  アプリによって更新すると、再起動しても有効なままになる。
  再起動後もマニフェストの設定より優先される
  ```kotlin
  val receiver = ComponentName(context, SampleBootReceiver::class.java)

  context.packageManager.setComponentEnabledSetting(
          receiver,
          PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
          PackageManager.DONT_KILL_APP
  )
  ```