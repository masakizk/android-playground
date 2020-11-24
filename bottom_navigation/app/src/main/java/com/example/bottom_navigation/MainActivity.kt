package com.example.bottom_navigation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.bottom_navigation.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

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