# Notification
## 通知を作成する
`NotificationCompat.Builder`により作成する
```kotlin
val builder = NotificationCompat.Builder(requireContext(), CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notifications_24px)
            .setContentTitle("Notification Title")
            .setContentText("Notification Content Text.\nHello from Fragment")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

val notification = builder.build()
```

## 通知する
`NotificationManager`のnotifyメソッドを使う
```kotlin
val notificationManager = NotificationManagerCompat.from(requireContext())
notificationManager.notify( /* id */ R.string.app_name, /* 通知 */notification)
```
同じ ID を渡すと、通知を更新できる

## Notification Channel
Android 8.0以上で通知を配信するには、  
`NotificationChannel`を`createNotificationChannel()` に渡すことにより、  
アプリの通知チャネルをシステムに登録しておく必要がある

```kotlin
if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
    val name = getString(R.string.channel_name)
    val descriptionText = getString(R.string.channel_description)
    val importance = NotificationManager.IMPORTANCE_DEFAULT

    val channel = NotificationChannel(CHANNEL_ID, name, importance)
        .apply { description = descriptionText }

    // 通知チャンネルをシステムに登録
    val notificationManager = NotificationManagerCompat.from(requireContext())
    notificationManager.createNotificationChannel(channel)
}
```