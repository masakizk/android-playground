# バイブレーション
### 準備

```kotlin
<uses-permission android:name="android.permission.VIBRATE"/>
```

### Vibratorを取得

```kotlin
private lateinit var vibrator: Vibrator

vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
```

### 一回振動させる

API level 26 (Android 8.0 Oreo) 未満の場合

```kotlin
vibrator.vibrate(300)
```

API level 26 (Android 8.0 Oreo) 以上の場合

```kotlin
val vibrationEffect = VibrationEffect.createOneShot(300, VibrationEffect.DEFAULT_AMPLITUDE)
vibrator.vibrate(vibrationEffect)
```

### 二回以上振動させる(リピートなし)

repeatを-1にする

API level 26 (Android 8.0 Oreo) 未満の場合

```kotlin
vibrator.vibrate(longArrayOf(0, 500, 400, 200), -1)
```

API level 26 (Android 8.0 Oreo) 以上の場合

```kotlin
val vibrationEffect = VibrationEffect.createWaveform(
        /* 秒数 */longArrayOf(0, 500, 400, 200),
        /* 振動の大きさ */intArrayOf(0, VibrationEffect.DEFAULT_AMPLITUDE, 0, VibrationEffect.DEFAULT_AMPLITUDE),
        /* repeat */-1
)
vibrator.vibrate(vibrationEffect)
```

### 二回以上振動させる(リピートあり)

repeatにリピートさせる部分の始まりのインデックスを設定する

API level 26 (Android 8.0 Oreo) 未満の場合

```kotlin
vibrator.vibrate(longArrayOf(0, 500, 400, 200), 2)
```

API level 26 (Android 8.0 Oreo) 以上の場合

```kotlin
val vibrationEffect = VibrationEffect.createWaveform(
        /* 秒数 */longArrayOf(0, 500, 400, 200),
        /* 振動の大きさ */intArrayOf(0, VibrationEffect.DEFAULT_AMPLITUDE, 0, VibrationEffect.DEFAULT_AMPLITUDE),
        /* repeat */2
)
vibrator.vibrate(vibrationEffect)
```

### バイブレーションを止める

リピートをかけると何もしないとアプリが終了するまで、振動し続ける

```kotlin
vibrator.cancel()
```