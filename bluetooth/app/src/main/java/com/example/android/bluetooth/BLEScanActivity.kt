package com.example.android.bluetooth

import android.bluetooth.BluetoothAdapter
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.android.bluetooth.databinding.ActivityBleScanBinding

class BLEScanActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityBleScanBinding
    private lateinit var mBluetoothAdapter: BluetoothAdapter
    private val mScanResults: MutableMap<String, ScanResult> = mutableMapOf()

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

        mBinding = ActivityBleScanBinding.inflate(layoutInflater)
        mBinding.apply {
            buttonStart.setOnClickListener { startScan() }

        }
        setContentView(mBinding.root)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun startScan() {
        if (mBluetoothAdapter.isDiscovering) mBluetoothAdapter.cancelDiscovery()
        mBluetoothAdapter.bluetoothLeScanner.startScan(object : ScanCallback() {
            override fun onScanResult(callbackType: Int, result: ScanResult?) {
                super.onScanResult(callbackType, result)
                val address = result?.device?.address ?: return
                mScanResults[address] = result

                val results = mScanResults.entries.sortedBy { it.key }.map { it.value }
                mBinding.textBluetoothList.text = ""
                results.forEach { result ->
                    val device = result.device
                    mBinding.textBluetoothList.append("${device.address} ${device.name} ${result.rssi} ${device.type}\n")
                }
            }

            override fun onScanFailed(errorCode: Int) {
                super.onScanFailed(errorCode)
                Log.d(TAG, "onScanFailed: $errorCode")
            }
        })
    }

    companion object {
        private const val TAG = "BLEScanActivity"
    }
}