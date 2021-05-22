package com.example.android.bluetooth

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.android.bluetooth.databinding.ActivityDiscoveryBinding
import com.example.android.bluetooth.device_list.BluetoothDeviceData
import com.example.android.bluetooth.device_list.BluetoothDeviceListAdapter
import com.example.android.bluetooth.device_list.BluetoothDeviceViewHolder
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class DiscoveryActivity : AppCompatActivity(), BluetoothDeviceViewHolder.CallbackListener {
    private lateinit var mBinding: ActivityDiscoveryBinding
    private lateinit var mBluetoothAdapter: BluetoothAdapter
    private val mDevices: MutableMap<String, BluetoothData> = mutableMapOf()
    private val mBluetoothDeviceListAdapter = BluetoothDeviceListAdapter(this)

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            when (intent.action) {
                BluetoothDevice.ACTION_FOUND -> {
                    val device =
                        intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                    val rssi = intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE)
                    val name = intent.getStringExtra(BluetoothDevice.EXTRA_NAME) ?: "無名の端末"

                    if (device == null) return
                    mDevices[device.address] = BluetoothData(device, rssi)

                    val devices = mDevices
                        .entries
                        .sortedBy { it.key }
                        .map {
                            val rssi = it.value.rssi
                            val device = it.value.device
                            BluetoothDeviceData(
                                name = device.name ?: "無名の端末",
                                address = device.address,
                                rssi = rssi.toInt()
                            )
                        }

                    mBluetoothDeviceListAdapter.submitList(devices)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

        registerReceiver(receiver, IntentFilter(BluetoothDevice.ACTION_FOUND))

        mBinding = ActivityDiscoveryBinding.inflate(layoutInflater)
        mBinding.apply {
            buttonStart.setOnClickListener { startDiscovery() }
            listDevice.adapter = mBluetoothDeviceListAdapter
            listDevice.layoutManager = LinearLayoutManager(this@DiscoveryActivity)
        }
        setContentView(mBinding.root)
    }

    private fun startDiscovery() {
//        mBluetoothAdapter.startDiscovery()

        lifecycleScope.launch {
            while (true) {
                if (mBluetoothAdapter.isDiscovering) mBluetoothAdapter.cancelDiscovery()
                mBluetoothAdapter.startDiscovery()
                delay(12 * 1000)
            }
        }
    }

    override fun onClick(data: BluetoothDeviceData) {
        val intent = ConnectGattActivity.createIntent(this, data.address)
        startActivity(intent)
    }

    private data class BluetoothData(
        val device: BluetoothDevice,
        val rssi: Short,
    )

    companion object {
        private const val TAG = "DiscoveryActivity"
    }
}