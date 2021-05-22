package com.example.android.bluetooth

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.android.bluetooth.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var mBluetoothAdapter: BluetoothAdapter
    private lateinit var mBinding: ActivityMainBinding

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        mBinding.apply {
            buttonDiscovery.setOnClickListener {
                val intent = Intent(this@MainActivity, DiscoveryActivity::class.java)
                startActivity(intent)
            }
            buttonBleScan.setOnClickListener {
                val intent = Intent(this@MainActivity, BLEScanActivity::class.java)
                startActivity(intent)
            }
        }
        setContentView(mBinding.root)

        requestPermissions(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
            ), 100
        )
    }
}