package com.example.android.bluetooth

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothProfile
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.android.bluetooth.databinding.ActivityGattBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ConnectGattActivity : AppCompatActivity() {
    private lateinit var mBinding: ActivityGattBinding
    private val macAddress get() = intent.getStringExtra(KEY_MAC_ADDRESS)
    private val mDevice get() = mBluetoothAdapter.getRemoteDevice(macAddress)

    private val mBluetoothAdapter: BluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityGattBinding.inflate(layoutInflater)
        mBinding.apply {
            buttonConnect.setOnClickListener { connect() }
            buttonConnectGatt.setOnClickListener { connectGatt() }
        }
        setContentView(mBinding.root)
    }

    private fun connectGatt() {
        if (mBluetoothAdapter.isDiscovering) mBluetoothAdapter.cancelDiscovery()
        mBinding.textDevice.text = "${mDevice?.name}"

        val bluetoothGatt = mDevice.connectGatt(this, false, object : BluetoothGattCallback() {
            override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
                super.onConnectionStateChange(gatt, status, newState)
                lifecycleScope.launch(Dispatchers.Main) {
                    when (newState) {
                        BluetoothProfile.STATE_CONNECTED -> {
                            mBinding.textState.text = "CONNECTED"
                        }
                        BluetoothProfile.STATE_DISCONNECTED -> {
                            Log.d(TAG, "onConnectionStateChange: DISCONNECTED")
                            mBinding.textState.text = "DISCONNECTED"
                        }
                    }
                }
            }

            override fun onReadRemoteRssi(gatt: BluetoothGatt?, rssi: Int, status: Int) {
                super.onReadRemoteRssi(gatt, rssi, status)
                lifecycleScope.launch(Dispatchers.Main) {
                    mBinding.textRssi.text = "$rssi"
                }
            }

        })

        bluetoothGatt.connect()
        lifecycleScope.launch {
            while (true) {
                bluetoothGatt.readRemoteRssi()
                delay(500)
            }
        }
    }

    private fun connect() {
        mDevice.createBond()
    }


    companion object {
        private const val TAG = "ConnectGattActivity"
        private const val KEY_MAC_ADDRESS = "MAC_ADDRESS"

        fun createIntent(context: Context, macAddress: String): Intent {
            return Intent(context, ConnectGattActivity::class.java).apply {
                putExtra(KEY_MAC_ADDRESS, macAddress)
            }
        }
    }
}