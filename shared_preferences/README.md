# Shared Preferences
## Shared Preferencesの種類
- ### 一つアクティビティに対する共有環境設定ファイル
  ```kotlin
  getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)
  ```
- ### 名前で識別される共有環境設定ファイル
  ```kotlin
  getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE)
  ```
## 値の保存
- `SharedPreferences`からエディターを取得  
- キーと値を設定し書き込む  
- `commit()`で閉じる
```kotlin
val editor = pref.edit()
with(editor) {
    putInt(COUNT_KEY, ++count)
    commit()
}
```
## 値の取得
```kotlin
pref.getInt(COUNT_KEY, 0)
```