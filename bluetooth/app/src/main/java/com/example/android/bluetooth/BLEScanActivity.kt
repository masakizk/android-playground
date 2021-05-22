package com.example.android.bluetooth

import android.bluetooth.BluetoothAdapter
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.android.bluetooth.databinding.ActivityBleScanBinding
import com.example.android.bluetooth.device_list.BluetoothDeviceData
import com.example.android.bluetooth.device_list.BluetoothDeviceListAdapter
import com.example.android.bluetooth.device_list.BluetoothDeviceViewHolder

class BLEScanActivity : AppCompatActivity(), BluetoothDeviceViewHolder.CallbackListener {

    private lateinit var mBinding: ActivityBleScanBinding
    private lateinit var mBluetoothAdapter: BluetoothAdapter
    private val mScanResults: MutableMap<String, ScanResult> = mutableMapOf()
    private val mBluetoothDeviceListAdapter = BluetoothDeviceListAdapter(this)
    private var mBluetoothScanCallback: ScanCallback? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

        mBinding = ActivityBleScanBinding.inflate(layoutInflater)
        mBinding.apply {
            listDevice.adapter = mBluetoothDeviceListAdapter
            listDevice.layoutManager = LinearLayoutManager(this@BLEScanActivity)
        }
        setContentView(mBinding.root)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            startScan()
        } else {
            throw RuntimeException()
        }
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun startScan() {
        mBinding.progressCircular.visibility = View.VISIBLE

        if (mBluetoothAdapter.isDiscovering) mBluetoothAdapter.cancelDiscovery()
        mBluetoothScanCallback = object : ScanCallback() {
            override fun onScanResult(callbackType: Int, result: ScanResult?) {
                super.onScanResult(callbackType, result)
                val address = result?.device?.address ?: return

                mScanResults[address] = result
                val results = mScanResults.values.sortedBy { it.device.address }
                val devices = results.map {
                    BluetoothDeviceData(
                        name = it.device.name ?: "無名の端末",
                        address = it.device.address,
                        rssi = it.rssi
                    )
                }
                mBluetoothDeviceListAdapter.submitList(devices)
                mBinding.progressCircular.visibility = View.GONE
            }

            override fun onScanFailed(errorCode: Int) {
                super.onScanFailed(errorCode)
                Toast.makeText(this@BLEScanActivity, "Failed: $errorCode", Toast.LENGTH_LONG).show()
                Log.d(TAG, "onScanFailed: $errorCode")
            }
        }
        mBluetoothAdapter.bluetoothLeScanner.startScan(mBluetoothScanCallback)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mBluetoothAdapter.bluetoothLeScanner.stopScan(mBluetoothScanCallback)
        }
    }

    override fun onClick(data: BluetoothDeviceData) {
        val intent = ConnectGattActivity.createIntent(this, data.address)
        startActivity(intent)
    }

    companion object {
        private const val TAG = "BLEScanActivity"
    }
}