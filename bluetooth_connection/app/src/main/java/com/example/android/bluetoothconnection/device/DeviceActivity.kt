package com.example.android.bluetoothconnection.device

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.android.bluetoothconnection.databinding.ActivityDeviceBinding
import com.example.android.bluetoothconnection.peripheral.PeripheralActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DeviceActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityDeviceBinding
    private lateinit var mBluetoothAdapter: BluetoothAdapter
    private var mBluetoothGatt: BluetoothGatt? = null
    private val mDeviceName: String get() = intent.getStringExtra(PARAM_DEVICE_NAME)!!
    private val mDeviceAddress: String get() = intent.getStringExtra(PARAM_DEVICE_ADDRESS)!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        mBinding = ActivityDeviceBinding.inflate(layoutInflater)
        mBinding.apply {
            textDeviceAddress.text = mDeviceAddress
            textDeviceName.text = mDeviceName
            editTextMessage.setText("HELLO WORLD")

            buttonConnect.setOnClickListener { connect() }
            buttonDisconnect.setOnClickListener { disconnect() }

            buttonSendMessage.setOnClickListener { sendMessage() }
        }
        setContentView(mBinding.root)
    }

    override fun onDestroy() {
        mBluetoothGatt?.close()
        super.onDestroy()
    }

    private fun connect() {
        val device = mBluetoothAdapter.getRemoteDevice(mDeviceAddress)
        mBluetoothGatt = device.connectGatt(this, false, object : BluetoothGattCallback() {
            override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
                super.onConnectionStateChange(gatt, status, newState)
                val connected = newState == BluetoothGatt.STATE_CONNECTED

                gatt?.discoverServices()

                lifecycleScope.launch(Dispatchers.Main) {
                    mBinding.apply {
                        buttonConnect.isEnabled = !connected
                        buttonDisconnect.isEnabled = connected
                    }
                }
            }

            override fun onServicesDiscovered(gatt: BluetoothGatt?, status: Int) {
                super.onServicesDiscovered(gatt, status)

                lifecycleScope.launch(Dispatchers.Main) {
                    mBinding.buttonSendMessage.isEnabled = true
                }
            }
        })
        mBluetoothGatt?.connect()
    }

    private fun sendMessage() {
        val gatt = mBluetoothGatt ?: return
        val service = gatt.getService(PeripheralActivity.UUID_SERVICE)
        val characteristic = service.getCharacteristic(PeripheralActivity.UUID_WRITE)
        characteristic.setValue(mBinding.editTextMessage.text.toString())
        gatt.writeCharacteristic(characteristic)
    }

    private fun disconnect() {
        mBluetoothGatt?.close()
    }

    companion object {
        private const val PARAM_DEVICE_NAME = "NAME"
        private const val PARAM_DEVICE_ADDRESS = "ADDRESS"

        fun createIntent(context: Context, deviceName: String, deviceAddress: String): Intent {
            return Intent(context, DeviceActivity::class.java).apply {
                putExtra(PARAM_DEVICE_NAME, deviceName)
                putExtra(PARAM_DEVICE_ADDRESS, deviceAddress)
            }
        }
    }
}