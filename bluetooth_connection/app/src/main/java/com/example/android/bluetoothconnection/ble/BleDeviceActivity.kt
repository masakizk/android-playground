package com.example.android.bluetoothconnection.ble

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.android.bluetoothconnection.databinding.ActivityBleDeviceBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import kotlin.math.min

class BleDeviceActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityBleDeviceBinding
    private lateinit var mBluetoothAdapter: BluetoothAdapter
    private var mBluetoothGatt: BluetoothGatt? = null
    private val mDeviceName: String get() = intent.getStringExtra(PARAM_DEVICE_NAME)!!
    private val mDeviceAddress: String get() = intent.getStringExtra(PARAM_DEVICE_ADDRESS)!!

    private var mMtu = 20

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        mBinding = ActivityBleDeviceBinding.inflate(layoutInflater)
        mBinding.apply {
            textDeviceAddress.text = mDeviceAddress
            textDeviceName.text = mDeviceName
            editTextMessage.setText("HELLO WORLD")

            buttonConnect.setOnClickListener { connect() }
            buttonDisconnect.setOnClickListener { disconnect() }

            buttonSendMessage.setOnClickListener {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                    sendMessage()
            }
        }

        setContentView(mBinding.root)
    }

    override fun onDestroy() {
        disconnect()
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

            override fun onCharacteristicWrite(
                gatt: BluetoothGatt?,
                characteristic: BluetoothGattCharacteristic?,
                status: Int
            ) {
                super.onCharacteristicWrite(gatt, characteristic, status)
            }

            override fun onMtuChanged(gatt: BluetoothGatt?, mtu: Int, status: Int) {
                super.onMtuChanged(gatt, mtu, status)
                if (gatt == null) return

                mMtu = mtu
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    sendMessage()
                }
            }
        })
        mBluetoothGatt?.connect()
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun sendMessage() {
        val gatt = mBluetoothGatt ?: return
        val bytes = mBinding.editTextMessage.text.toString().toByteArray()
        if (bytes.size > 512) {
            lifecycleScope.launch(Dispatchers.Main) {
                Toast.makeText(this@BleDeviceActivity, "message is too long", Toast.LENGTH_LONG)
                    .show()
            }
            return
        }
        if (mMtu < 512) {
            // LOLLIPOP以降では512までリクエスト可能
            gatt.requestMtu(512)
            return
        }

        lifecycleScope.launch(Dispatchers.Main) {
            Toast.makeText(
                this@BleDeviceActivity,
                "MTU: $mMtu\ndata: ${bytes.size}bytes",
                Toast.LENGTH_LONG
            ).show()
        }

        val service = gatt.getService(BlePeripheralActivity.UUID_SERVICE)
        val characteristic = service.getCharacteristic(BlePeripheralActivity.UUID_WRITE)
        for (i in 0..bytes.size step 100) {
            val to = min(i + 100, bytes.size)
            characteristic.value = bytes.copyOfRange(i, to)
            gatt.writeCharacteristic(characteristic)
        }
    }

    private fun disconnect() {
        mBluetoothGatt?.disconnect()
        mBluetoothGatt?.close()
    }

    companion object {
        private const val PARAM_DEVICE_NAME = "NAME"
        private const val PARAM_DEVICE_ADDRESS = "ADDRESS"
        private const val TAG = "DeviceActivity"

        fun createIntent(context: Context, deviceName: String, deviceAddress: String): Intent {
            return Intent(context, BleDeviceActivity::class.java).apply {
                putExtra(PARAM_DEVICE_NAME, deviceName)
                putExtra(PARAM_DEVICE_ADDRESS, deviceAddress)
            }
        }
    }
}