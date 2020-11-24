# Preferences
[https://developer.android.com/guide/topics/ui/settings?hl=ja](https://developer.android.com/guide/topics/ui/settings?hl=ja)
- [準備](#準備)
- [Get Started](#get-started)
- [ダイアログを用いた設定](#ダイアログを用いた設定)
  * [EditText](#edittext)
  * [List(ひとつだけ選択)](#List(ひとつだけ選択))
  * [Multiple Select(複数選択)](#Multiple-Select(複数選択))
- [ウィジェットを用いた設定](#ウィジェットを用いた設定)
  * [Checkbox](#checkbox)
  * [Switch](#switch)
  * [DropDown](#dropdown)
  * [SeekBar](#seekbar)
- [設定画面の移動](#設定画面の移動)
- [保存した値を使用する](#保存した値を使用する)
  * [Shared Preferences](#shared-preferences)
  * [Preferenceの読み取り](#Preferenceの読み取り)
  * [onSharedPreferenceChanged](#onsharedpreferencechanged)
  * [onPreferenceChange](#onpreferencechange)

<small><i><a href='http://ecotrust-canada.github.io/markdown-toc/'>Table of contents generated with markdown-toc</a></i></small>

## 準備
```kotlin
implementation "androidx.preference:preference-ktx:1.1.1"
```

## Get Started
### 1. xml > preferences.xmlを作成

    ```xml
    <?xml version="1.0" encoding="utf-8"?>
    <PreferenceScreen xmlns:android="http://schemas.android.com/apk/res/android"
        xmlns:app="http://schemas.android.com/apk/res-auto">
        <PreferenceCategory
            app:key="help_category"
            app:title="Help">

            <Preference
                app:key="feedback"
                app:summary="Report technical issues or suggest new features"
                app:title="Send feedback" />

        </PreferenceCategory>
    </PreferenceScreen>
    ```

### 2. `PreferenceFragmentCompat()`を継承し、設定のリソースを指定

    ```kotlin
    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.preferences, rootKey)
        }
    }
    ```

### 3. Fragmentを画面に埋め込む

    ```kotlin
    supportFragmentManager
        .beginTransaction()
        .replace(binding.fragmentContainer.id, SettingsFragment())
        .commit()
    ```

## ダイアログを用いた設定
### EditText
```xml
<EditTextPreference
      app:key="edittext"
      app:title="EditText preferences"
      app:useSimpleSummaryProvider="true"
      app:dialogTitle="This title can be changed!"/>
```

### List(ひとつだけ選択)
entries: 画面に表示される値
entryValues: 設定に保存される値

```xml
<ListPreference
    app:key="list"
    app:title="List preferences"
    app:useSimpleSummaryProvider="true"
    app:entries="@array/entries"
    app:entryValues="@array/entry_values"
    app:dialogTitle="Choose one option"/>
```

### Multiple Select(複数選択)
```xml
<MultiSelectListPreference
    app:key="multi_select_list"
    app:title="Multi-select list preference"
    app:summary="Shows a dialog with multiple choice options"
    app:entries="@array/entries"
    app:entryValues="@array/entry_values"
    app:dialogTitle="Choose some options"/>
```

## ウィジェットを用いた設定
### Checkbox
```xml
<CheckBoxPreference
  app:key="checkbox"
  app:title="Checkbox preference"
  app:summary="Tap anywhere in this preference to toggle state"/>
```

### Switch
```xml
<SwitchPreferenceCompat
    app:key="switch"
    app:title="Switch preference"
    app:summary="Tap anywhere in this preference to toggle state"/>
```

### DropDown
```xml
<DropDownPreference
  app:key="dropdown"
  app:title="Dropdown preference"
  app:useSimpleSummaryProvider="true"
  app:entries="@array/entries"
  app:entryValues="@array/entry_values"/>
```

### SeekBar
```xml
<SeekBarPreference
    app:key="seekbar"
    app:title="Seekbar preference"
    app:defaultValue="20"/>
```

## 設定画面の移動
`android:key`を設定し、タップされたものに応じてFragmentを生成する
```xml
<Preference
    android:title="@string/dialogs"
    android:key="@string/dialogs"/>

<Preference
    android:title="@string/widgets"
    android:key="@string/widgets" />

<Preference
    android:title="@string/advanced_attributes"
    android:key="@string/advanced_attributes" />
```

```kotlin
// キーによって移動先のFragmentを生成する
override fun onPreferenceTreeClick(preference: Preference): Boolean {
    when (preference.key) {
        getString(R.string.dialogs) -> loadFragment(DialogPreferencesFragment())
        getString(R.string.widgets) -> loadFragment(WidgetPreferencesFragment())
        getString(R.string.advanced_attributes) -> loadFragment(AdvancedPreferencesFragment())
        else -> return super.onPreferenceTreeClick(preference)
    }

    return true
}

// Fragmentの埋め込み
protected fun loadFragment(fragment: Fragment){
    requireActivity()
        .supportFragmentManager
        .beginTransaction()
        .replace(R.id.fragment_container, fragment)
        .addToBackStack("main")
        .commit()
}
```

## 保存した値を使用する
### Shared Preferences

デフォルトでは`SharedPreferences` を利用して値を保存する。

### Preferenceの読み取り

```kotlin
lateinit var sharedPreferences: SharedPreferences

sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)

val key = getString(R.string.saved_boolean_value)
val savedValue = sharedPreferences.getBoolean(key, true)
```

### onSharedPreferenceChanged

- すべてのPreferenceの変更をリッスンする
- 値が保存されたあとに呼ばれる
- SharedPreferenceを使用しているときにのみ呼び出される

```kotlin
class MainActivity : AppCompatActivity(), SharedPreferences.OnSharedPreferenceChangeListener {
```

```kotlin
override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
    val savedKey = getString(R.string.saved_boolean_value)
    val savedValue = sharedPreferences.getBoolean(savedKey, true)
    Toast.makeText(this,"key: saved_boolean_value value:${savedValue}",Toast.LENGTH_LONG).show()
}
```

ライフサイクルを適切に管理するために、リスナーの登録と解除を`onResume()`, `onPause()`コールバックで行う

```kotlin
override fun onResume() {
    super.onResume()
    sharedPreferences.registerOnSharedPreferenceChangeListener(this)
}

override fun onPause() {
    super.onPause()
    sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
}
```

### onPreferenceChange

- Preferenceごとに設定
- 保存されている値を変更しようとしたときに呼ばれる
    - 保留中の値が現在保存されている場合も呼ばれる
- SharedPreferences, PreferenceDataStoreを使用した場合に呼ばれる

```kotlin
class SettingsFragment : BasePreferenceFragment(), Preference.OnPreferenceChangeListener {

		override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

				// 特定のPreferenceに対して、リスナーを登録する
        val key = getString(R.string.saved_boolean_value)
        val checkbox = findPreference<CheckBoxPreference>(key)
        checkbox?.onPreferenceChangeListener = this
    }

		override fun onPreferenceChange(preference: Preference, newValue: Any): Boolean {
        Toast.makeText(requireContext(),"key:${preference.key} value:$newValue",Toast.LENGTH_LONG).show()
        return true
    }
}
```