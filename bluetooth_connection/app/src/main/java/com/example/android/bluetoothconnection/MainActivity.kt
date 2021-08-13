package com.example.android.bluetoothconnection

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.android.bluetoothconnection.ble.BleDeviceListActivity
import com.example.android.bluetoothconnection.ble.BlePeripheralActivity
import com.example.android.bluetoothconnection.databinding.ActivityMainBinding
import com.example.android.bluetoothconnection.socket.image.ImageSocketDeviceListActivity
import com.example.android.bluetoothconnection.socket.image.ImageTransferPeripheralActivity
import com.example.android.bluetoothconnection.socket.text.SocketDeviceListActivity
import com.example.android.bluetoothconnection.socket.text.SocketPeripheralActivity

class MainActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        mBinding.apply {
            buttonBleCentral.setOnClickListener {
                val intent = Intent(this@MainActivity, BleDeviceListActivity::class.java)
                startActivity(intent)
            }

            buttonBlePeripheral.setOnClickListener {
                val intent = Intent(this@MainActivity, BlePeripheralActivity::class.java)
                startActivity(intent)
            }

            buttonSocketCentral.setOnClickListener {
                val intent = Intent(this@MainActivity, SocketDeviceListActivity::class.java)
                startActivity(intent)
            }

            buttonSocketPeripheral.setOnClickListener {
                val intent = SocketPeripheralActivity.createIntent(this@MainActivity)
                startActivity(intent)
            }

            buttonImageSocketCentral.setOnClickListener {
                val intent = Intent(this@MainActivity, ImageSocketDeviceListActivity::class.java)
                startActivity(intent)
            }

            buttonImageSocketPeripheral.setOnClickListener {
                val intent = ImageTransferPeripheralActivity.createIntent(this@MainActivity)
                startActivity(intent)
            }
        }
    }
}