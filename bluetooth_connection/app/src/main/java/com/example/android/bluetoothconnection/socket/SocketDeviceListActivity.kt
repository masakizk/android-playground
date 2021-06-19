package com.example.android.bluetoothconnection.socket

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.android.bluetoothconnection.databinding.ActivityDeviceListBinding
import com.example.android.bluetoothconnection.devices.DeviceListAdapter
import com.example.android.bluetoothconnection.devices.DeviceViewHolder
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SocketDeviceListActivity : AppCompatActivity(), DeviceViewHolder.CallbackListener {
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
                            ?: return
                    addDevice(device)
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
            recyclerViewDevices.layoutManager = LinearLayoutManager(this@SocketDeviceListActivity)
        }
        setContentView(mBinding.root)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                ), 100
            )
        }

        startDiscovery()
    }

    override fun onDestroy() {
        mBluetoothAdapter.cancelDiscovery()
        unregisterReceiver(receiver)
        super.onDestroy()
    }

    private fun addDevice(device: BluetoothDevice) {
        if (device.address == null || device.name == null) return
        mDevices[device.address] = device

        val devices = mDevices
            .values
            .sortedBy { it.address }

        mBluetoothDeviceListAdapter.submitList(devices)
    }

    private fun startDiscovery() {
        lifecycleScope.launch {
            while (true) {
                if (mBluetoothAdapter.isDiscovering) mBluetoothAdapter.cancelDiscovery()
                mBluetoothAdapter.startDiscovery()
                delay(12 * 1000)
            }
        }
    }

    override fun onClick(device: BluetoothDevice) {
        val intent = SocketDeviceActivity.createIntent(
            this,
            deviceName = device.name,
            deviceAddress = device.address,
        )
        startActivity(intent)
    }
}