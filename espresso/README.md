# Espresso
[Espresso の基本 - developer.android.com](https://developer.android.com/training/testing/espresso/basics?hl=ja)

```groovy
androidTestImplementation 'androidx.test.espresso:espresso-core:3.3.0'
androidTestImplementation 'com.android.support.test:rules:1.0.2'
androidTestImplementation 'androidx.test:runner:1.1.0'
```

### テストを作成

```kotlin
@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @Rule
    @JvmField
    val mActivityTestRule = ActivityTestRule(MainActivity::class.java)
}
```

### Viewの特定

```kotlin
// IDを使って取得
onView(withId(R.id.text_hello_world))

// 複数条件を使った絞り込み
onView(allOf(withId(R.id.my_view), withText("Hello!")))

// notを使って条件を反転
onView(allOf(withId(R.id.my_view), not(withText("Unwanted"))))
```

### アサーション

`check()`を用いることでアサーションができる

```kotlin
onView(withId(R.id.text_hello_world))
            .check(matches(withText("Hello World!")))
```

### クリック

```kotlin
onView(withId(R.id.button_change_message)).perform(click())
```

### テキストの入力

```kotlin
// キーボードでViewが隠れてエラーが発生することがあるので、入力後にキーボードを閉じる
onView(withId(R.id.et_message)).perform(typeText("Android"), closeSoftKeyboard())
```