package com.example.android.bluetoothconnection.socket

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.android.bluetoothconnection.databinding.ActivitySocketDeviceBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SocketDeviceActivity : AppCompatActivity() {
    private lateinit var mBinding: ActivitySocketDeviceBinding
    private val mDeviceName: String get() = intent.getStringExtra(PARAM_DEVICE_NAME)!!
    private val mDeviceAddress: String get() = intent.getStringExtra(PARAM_DEVICE_ADDRESS)!!
    private lateinit var mBluetoothAdapter: BluetoothAdapter
    private var mBluetoothSocket: BluetoothSocket? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivitySocketDeviceBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

        mBinding.apply {
            textDeviceAddress.text = mDeviceAddress
            textDeviceName.text = mDeviceName
            editTextMessage.setText("HELLO WORLD")

            buttonConnect.setOnClickListener { connect() }
            buttonDisconnect.setOnClickListener { disconnect() }
            buttonSendMessage.setOnClickListener { sendMessage() }
        }
    }

    private fun connect() {
        val device = mBluetoothAdapter.getRemoteDevice(mDeviceAddress)
        if (device == null) {
            Toast.makeText(this, "cannot find", Toast.LENGTH_LONG).show()
            return
        }

        mBluetoothSocket =
            device.createRfcommSocketToServiceRecord(SocketPeripheralActivity.UUID_SERVICE)

        if (mBluetoothSocket == null) return

        lifecycleScope.launch(Dispatchers.IO) {
            kotlin.runCatching {
                mBluetoothAdapter.cancelDiscovery()
                mBluetoothSocket!!.connect()
            }.onSuccess {
                withContext(Dispatchers.Main) {
                    mBinding.buttonConnect.isEnabled = false
                    mBinding.buttonDisconnect.isEnabled = true
                    mBinding.buttonSendMessage.isEnabled = true
                }
            }.onFailure {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@SocketDeviceActivity,
                        "failed in connect",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun disconnect() {
        mBluetoothSocket?.close()
    }

    private fun sendMessage() {
        val socket = mBluetoothSocket ?: return
        val message = mBinding.editTextMessage.text.toString()
        socket.outputStream.write(message.toByteArray())
    }

    companion object {
        private const val PARAM_DEVICE_NAME = "NAME"
        private const val PARAM_DEVICE_ADDRESS = "ADDRESS"

        fun createIntent(context: Context, deviceName: String, deviceAddress: String): Intent {
            return Intent(context, SocketDeviceActivity::class.java).apply {
                putExtra(PARAM_DEVICE_NAME, deviceName)
                putExtra(PARAM_DEVICE_ADDRESS, deviceAddress)
            }
        }
    }
}