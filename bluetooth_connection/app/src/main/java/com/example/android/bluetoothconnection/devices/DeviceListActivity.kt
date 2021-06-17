package com.example.android.bluetoothconnection.devices

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.android.bluetoothconnection.databinding.ActivityDeviceListBinding
import com.example.android.bluetoothconnection.device.DeviceActivity
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class DeviceListActivity : AppCompatActivity(), DeviceViewHolder.CallbackListener {
    private lateinit var mBinding: ActivityDeviceListBinding
    private lateinit var mBluetoothAdapter: BluetoothAdapter
    private val mDevices: MutableMap<String, BluetoothDevice> = mutableMapOf()
    private val mBluetoothDeviceListAdapter = DeviceListAdapter(this)

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            when (intent.action) {
                BluetoothDevice.ACTION_FOUND -> {
                    val device =
                        intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)

                    Log.d(TAG, "onReceive: ${device?.name} ${device?.address}")
                    if (device == null || device.name == null) return
                    mDevices[device.address] = device

                    val devices = mDevices
                        .values
                        .sortedBy { it.address }

                    mBluetoothDeviceListAdapter.submitList(devices)
                }
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

        registerReceiver(receiver, IntentFilter(BluetoothDevice.ACTION_FOUND))

        mBinding = ActivityDeviceListBinding.inflate(layoutInflater)
        mBinding.apply {
            recyclerViewDevices.adapter = mBluetoothDeviceListAdapter
            recyclerViewDevices.layoutManager = LinearLayoutManager(this@DeviceListActivity)
        }
        setContentView(mBinding.root)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                ), 100
            )
        }

        lifecycleScope.launch {
            while (true) {
                if (mBluetoothAdapter.isDiscovering) mBluetoothAdapter.cancelDiscovery()
                Log.d(TAG, "onCreate: ${mBluetoothAdapter.startDiscovery()}")
                delay(12 * 1000)
            }
        }
    }

    override fun onClick(device: BluetoothDevice) {
        val intent = DeviceActivity.createIntent(
            this,
            deviceName = device.name,
            deviceAddress = device.address
        )
        startActivity(intent)
    }

    companion object {
        private const val TAG = "DeviceListActivity"
    }
}