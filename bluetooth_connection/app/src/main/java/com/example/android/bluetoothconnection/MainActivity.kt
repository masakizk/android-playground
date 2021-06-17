package com.example.android.bluetoothconnection

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.android.bluetoothconnection.databinding.ActivityMainBinding
import com.example.android.bluetoothconnection.devices.DeviceListActivity
import com.example.android.bluetoothconnection.peripheral.PeripheralActivity

class MainActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        mBinding.apply {
            buttonCentral.setOnClickListener {
                val intent = Intent(this@MainActivity, DeviceListActivity::class.java)
                startActivity(intent)
            }

            buttonPeripheral.setOnClickListener {
                val intent = Intent(this@MainActivity, PeripheralActivity::class.java)
                startActivity(intent)
            }
        }
    }
}