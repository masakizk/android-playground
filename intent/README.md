# Intent
Intentはメッセージオブジェクトであり、以下のことを実現するために利用される
- 他のアプリケーションコンポーネントにアクションを依頼
- 異なるコンポーネント間での通信

## 明示的なインテント
**同一アプリケーション**の2つの`Activity`で通信する
### 画面遷移

AndroidManifestにアクティビティを追加

```xml
<application
    ...>
    ...
    <activity android:name=".SecondActivity" />
</application>
```

インテントを作成し、アクティビティを開始する

```kotlin
// インテントを作成
val intent = Intent(this, SecondActivity::class.java)
// データをセット
val message = "Hello World"
intent.putExtra("message", message)
// 遷移先の画面を起動
startActivity(intent)
```
## 暗黙的なインテント
アクションを指定し、それを実行できる他のアプリケーションを起動する
```kotlin
val sendIntent = Intent().apply {
    action = Intent.ACTION_SEND
    putExtra(Intent.EXTRA_TEXT, "Message")
    type = "text/plain"
}

// インテントを解決するアクティビティを確認
if (sendIntent.resolveActivity(packageManager) != null) {
    startActivity(sendIntent)
}
```
## インテントフィルター
暗黙的なインテントを受け取るために、AndroidManifestで<intent-filter>を作成する
- `<action>`
    受け入れるインテントのアクション
- `<data>`
    受け入れるデータタイプを
    データURI（schema, host, port, path）とMIMEタイプによって宣言
- `<category>`
    受け入れるインテントカテゴリ
```xml
<activity android:name="ShareActivity">
    <intent-filter>
        <action android:name="android.intent.action.SEND"/>
        <category android:name="android.intent.category.DEFAULT"/>
        <data android:mimeType="text/plain"/>
    </intent-filter>
</activity>
```
複数のインテントフィルターを用意することで、複数の種類のインテントに対応できる

```xml
<activity android:name="ShareActivity">
    <!-- This activity handles "SEND" actions with text data -->
    <intent-filter>
        <action android:name="android.intent.action.SEND"/>
        <category android:name="android.intent.category.DEFAULT"/>
        <data android:mimeType="text/plain"/>
    </intent-filter>
    <!-- This activity also handles "SEND" and "SEND_MULTIPLE" with media data -->
    <intent-filter>
        <action android:name="android.intent.action.SEND"/>
        <action android:name="android.intent.action.SEND_MULTIPLE"/>
        <category android:name="android.intent.category.DEFAULT"/>
        <data android:mimeType="application/vnd.google.panorama360+jpg"/>
        <data android:mimeType="image/*"/>
        <data android:mimeType="video/*"/>
    </intent-filter>
</activity>
```
### 外部のアプリにインテントを公開しない
`android:exported=false`を指定する
```xml
 <activity android:name=".SecondActivity" android:exported="false" >
```