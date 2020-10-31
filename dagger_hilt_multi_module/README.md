### **問題点**

モジュール間の循環依存が禁止されているためナビゲーションができない

### 解決法

すべてのモジュールが依存する`core`モジュールを作成し、

そこにナビゲーションに関するインターフェースを用意する

```kotlin
interface Router {
    fun navigateToSecondFragment(controller: NavController)

    fun navigateToFirstFragment(controller: NavController)
}
```

`app`モジュールのナビゲーショングラフで遷移を定義する

```kotlin
<navigation xmlns:android="http://schemas.android.com/apk/res/android"
    ...>

    <fragment
        android:id="@+id/FirstFragment"
        ...>
        <action
            android:id="@+id/action_second_fragment"
            app:destination="@id/SecondFragment"/>
    </fragment>

    <fragment
        android:id="@+id/SecondFragment"
        ...>
        <action
            android:id="@+id/action_first_fragment"
            app:destination="@id/FirstFragment" />
    </fragment>
</navigation>
```

インターフェースを`app`モジュールで実装、DIする

```kotlin
class ApplicationRouter : Router {
    override fun navigateToSecondFragment(controller: NavController, message: String) {
        val action = FirstFragmentDirections.actionSecondFragment()
				controller.navigate()
    }

    override fun navigateToFirstFragment(controller: NavController, message: String) {
        val action = SecondFragmentDirections.actionFirstFragment()
				controller.navigate(action)
    }
}
```

```kotlin
@Module
@InstallIn(FragmentComponent::class)
object MyFragmentComponent{
    @Provides
    fun provideRouter(): Router{
        return ApplicationRouter()
    }
}
```

### 遷移時の値の受け渡し

1. appモジュールの遷移アクションで引数を定義

    ```xml
    <fragment
        android:id="@+id/FirstFragment"
    		...>
        <action
            android:id="@+id/action_second_fragment"
            app:destination="@id/SecondFragment">
            <argument
                android:name="message"
                app:argType="string" />
        </action>

    </fragment>
    ```

    ```kotlin
    controller.navigate(
        FirstFragmentDirections.actionSecondFragment(
            message = message
        )
    )
    ```

2. 各モジュールでナビゲーショングラフを作成し、引数を定義

    ```xml
    <fragment
        android:id="@+id/secondFragment"
        android:name="com.example.banana.SecondFragment"
        android:label="fragment_second"
        tools:layout="@layout/fragment_second">
        <argument
            android:name="message"
            app:argType="string" />
    </fragment>
    ```

    Safe Argを生成

    ```groovy
    plugins {
    	id 'androidx.navigation.safeargs.kotlin'
    }
    ```

    `nabArgs()`で引数を受け取る

    ```kotlin
    private val args: SecondFragmentArgs by navArgs()
    // getにより、argsが初期化される前にアクセスするのを防ぐ
    private val message get() = args.message
    ```

### 新しく画面を作成するときに値を渡す

`Fragment`はコンストラクタにより値を渡そうとすると、

画面が破棄されたときなどにその値が失われてしまう。

なので、`argument`を介して渡す必要がある。

```kotlin
class SecondFragment : Fragment() {
    companion object {
        fun newInstance(args: SecondFragmentArgs): Fragment {
            return SecondFragment().apply {
                arguments = args.toBundle()
            }
        }
    }
```

同様にappモジュールのナビゲーションに関する実装の中で値を渡す

```kotlin
override fun newSecondFragment(message: String): Fragment {
    val args = SecondFragmentArgs(message = message)
    return SecondFragment.newInstance(args)
}
```

以下のようにして、フラグメントを作成する

```kotlin
val fragment = router.newSecondFragment("message")
```
