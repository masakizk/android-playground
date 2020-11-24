# Bottom Navigation

### 準備

```kotlin
dependencies {
    // Material
    implementation 'com.google.android.material:material:1.2.1'
    // Navigation
    implementation 'androidx.navigation:navigation-ui-ktx:2.3.1'
    implementation 'androidx.navigation:navigation-fragment-ktx:2.3.1'
}
```

### Bottom Navigationをレイアウトに追加

```xml
<com.google.android.material.bottomnavigation.BottomNavigationView
        android:id="@+id/nav_view"
        android:background="?android:attr/windowBackground"
        app:menu="@menu/bottom_nav_menu" />
```

### Menuを作成

res/menu/bottom_nav_menu.xml

```xml
<?xml version="1.0" encoding="utf-8"?>
<menu xmlns:android="http://schemas.android.com/apk/res/android">

    <item
        android:id="@+id/navigation_home"
        android:icon="@drawable/ic_home_black_24dp"
        android:title="@string/title_home" />

    <item
        android:id="@+id/navigation_dashboard"
        android:icon="@drawable/ic_dashboard_black_24dp"
        android:title="@string/title_dashboard" />

    <item
        android:id="@+id/navigation_notifications"
        android:icon="@drawable/ic_notifications_black_24dp"
        android:title="@string/title_notifications" />

</menu>
```

### Navigationを作成

menuのidと一致させる

```xml
<?xml version="1.0" encoding="utf-8"?>
<navigation xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:id="@+id/mobile_navigation"
    app:startDestination="@+id/navigation_home">

    <fragment
        android:id="@+id/navigation_home"
        android:name="com.example.bottom_navigation.ui.HomeFragment"
        android:label="@string/title_home"
        tools:layout="@layout/fragment_home" />

    <fragment
        android:id="@+id/navigation_dashboard"
        android:name="com.example.bottom_navigation.ui.DashboardFragment"
        android:label="@string/title_dashboard"
        tools:layout="@layout/fragment_dashboard" />

    <fragment
        android:id="@+id/navigation_notifications"
        android:name="com.example.bottom_navigation.ui.NotificationsFragment"
        android:label="@string/title_notifications"
        tools:layout="@layout/fragment_notifications" />
</navigation>
```

### Bottom NavigationとNavigationを対応させる

```kotlin
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        ... 

        val navController = findNavController(R.id.nav_host_fragment)

        // トップレベルの遷移先となる Menu ID を渡す
        val topLevelDestinationIds = setOf(R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
        val appBarConfiguration = AppBarConfiguration(topLevelDestinationIds)

        // NavControllerとActionBarを対応付け
        setupActionBarWithNavController(navController, appBarConfiguration)

        // BottomNavigationViewとNavControllerを対応付
        binding.navView.setupWithNavController(navController)
    }
}
```