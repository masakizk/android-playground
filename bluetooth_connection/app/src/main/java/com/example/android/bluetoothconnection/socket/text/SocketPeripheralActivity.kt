package com.example.android.bluetoothconnection.socket.text

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.android.bluetoothconnection.databinding.ActivitySocketPeripheralBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.*

class SocketPeripheralActivity : AppCompatActivity() {
    private lateinit var mBinding: ActivitySocketPeripheralBinding
    private lateinit var mBluetoothAdapter: BluetoothAdapter
    private var mBluetoothServerSocket: BluetoothServerSocket? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivitySocketPeripheralBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

        mBluetoothServerSocket =
            mBluetoothAdapter.listenUsingRfcommWithServiceRecord("MYAPP", UUID_SERVICE)


        val discoverableIntent = Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE)
            .apply { putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300) }

        startListening()

        startActivity(discoverableIntent)
    }

    private fun startListening() = lifecycleScope.launch(Dispatchers.IO) {
        var shouldLoop = true
        while (shouldLoop) {
            kotlin.runCatching {
                val socket = mBluetoothServerSocket?.accept() ?: return@launch
                withContext(Dispatchers.Main) { connected(socket) }
                receive(socket)
                shouldLoop = false
            }.onFailure {
                shouldLoop = false
            }
        }
    }

    private fun connected(socket: BluetoothSocket) {
        val device = socket.remoteDevice ?: return
        mBinding.textConnectedDevice.text = device.name ?: device.address
    }

    private fun receive(socket: BluetoothSocket) = lifecycleScope.launch(Dispatchers.IO) {
        while (true) {
            try {
                val buffer = ByteArray(1024)
                val bytes: Int = socket.inputStream.read(buffer)

                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@SocketPeripheralActivity,
                        "$bytes bytes",
                        Toast.LENGTH_LONG
                    ).show()
                    mBinding.textReceivedMessage.text = String(buffer)
                }
            } catch (e: IOException) {
                Log.e(TAG, "receive: error while reading data", e)

                withContext(Dispatchers.Main) {
                    Toast
                        .makeText(this@SocketPeripheralActivity, "done", Toast.LENGTH_LONG)
                        .show()
                }
                break
            }
        }
    }

    companion object {
        private const val TAG = "SocketPeripheralActivit"
        val UUID_SERVICE: UUID = UUID.fromString("a9d158bb-9007-4fe3-b5d2-d3696a3eb067")

        fun createIntent(context: Context): Intent {
            return Intent(context, SocketPeripheralActivity::class.java)
        }
    }
}