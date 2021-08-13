package com.example.android.bluetoothconnection.ble

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.android.bluetoothconnection.databinding.ActivityDeviceListBinding
import com.example.android.bluetoothconnection.devices.DeviceListAdapter
import com.example.android.bluetoothconnection.devices.DeviceViewHolder

class BleDeviceListActivity : AppCompatActivity(), DeviceViewHolder.CallbackListener {
    private lateinit var mBinding: ActivityDeviceListBinding
    private lateinit var mBluetoothAdapter: BluetoothAdapter
    private val mDevices: MutableMap<String, BluetoothDevice> = mutableMapOf()
    private val mBluetoothDeviceListAdapter = DeviceListAdapter(this)
    private var mBluetoothScanCallback: ScanCallback? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

        mBinding = ActivityDeviceListBinding.inflate(layoutInflater)
        mBinding.apply {
            recyclerViewDevices.adapter = mBluetoothDeviceListAdapter
            recyclerViewDevices.layoutManager = LinearLayoutManager(this@BleDeviceListActivity)
        }
        setContentView(mBinding.root)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                ), 100
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            100 -> {
                scan()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mBluetoothAdapter.bluetoothLeScanner.stopScan(mBluetoothScanCallback)
        }
    }

    override fun onClick(device: BluetoothDevice) {
        val intent = BleDeviceActivity.createIntent(
            this,
            deviceName = device.name,
            deviceAddress = device.address
        )
        startActivity(intent)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun scan() {
        if (mBluetoothAdapter.isDiscovering) mBluetoothAdapter.cancelDiscovery()
        mBluetoothScanCallback = object : ScanCallback() {
            override fun onScanResult(callbackType: Int, result: ScanResult?) {
                super.onScanResult(callbackType, result)
                val device = result?.device ?: return
                if (device.address == null || device.name == null) return

                mDevices[device.address] = device

                val devices = mDevices
                    .values
                    .sortedBy { it.address }

                mBluetoothDeviceListAdapter.submitList(devices)
            }

            override fun onScanFailed(errorCode: Int) {
                super.onScanFailed(errorCode)
                Toast.makeText(this@BleDeviceListActivity, "Failed: $errorCode", Toast.LENGTH_LONG)
                    .show()
                Log.d(TAG, "onScanFailed: $errorCode")
            }
        }
        mBluetoothAdapter.bluetoothLeScanner.startScan(mBluetoothScanCallback)
    }

    companion object {
        private const val TAG = "DeviceListActivity"
    }
}