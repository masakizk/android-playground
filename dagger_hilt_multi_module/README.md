# DaggerHilt - マルチモジュール
## 問題点
モジュール間の循環依存が禁止されているためナビゲーションができない

## 解決策
すべてのモジュールが依存する`core`モジュールを作成し、  
そこにナビゲーションに関するインターフェースを用意する
```kotlin
interface Router {
    fun navigateToSecondFragment(controller: NavController, message: String)

    fun navigateToFirstFragment(controller: NavController, message: String)
}
```