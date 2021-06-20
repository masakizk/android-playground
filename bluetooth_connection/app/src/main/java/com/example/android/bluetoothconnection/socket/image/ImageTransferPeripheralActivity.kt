package com.example.android.bluetoothconnection.socket.image

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.android.bluetoothconnection.databinding.ActivityImagePeripheralBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.lang.Integer.parseInt
import java.util.*

class ImageTransferPeripheralActivity : AppCompatActivity() {
    private lateinit var mBinding: ActivityImagePeripheralBinding
    private lateinit var mBluetoothAdapter: BluetoothAdapter
    private var mBluetoothServerSocket: BluetoothServerSocket? = null
    private var mBluetoothSocket: BluetoothSocket? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityImagePeripheralBinding.inflate(layoutInflater)
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
                mBluetoothSocket = socket
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
        var totalNumBytes = 0
        var flag = true
        var buffer: ByteArray? = null
        var bytePosition = 0

        while (true) {
            if (flag) {
                try {
                    val tmp = ByteArray(socket.inputStream.available())
                    if (socket.inputStream.read(tmp) > 0) {
                        totalNumBytes = parseInt(String(tmp, Charsets.UTF_8))
                        buffer = ByteArray(totalNumBytes)
                        flag = false
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "receive: error while reading data", e)
                    continue
                }
                continue
            }

            try {
                if (buffer == null) break

                val data = ByteArray(socket.inputStream.available())
                val byteCount = socket.inputStream.read(data)
                System.arraycopy(data, 0, buffer, bytePosition, byteCount)
                bytePosition += byteCount

                if (bytePosition == totalNumBytes) {
                    setImage(buffer, totalNumBytes)

                    buffer = null
                    totalNumBytes = 0
                    bytePosition = 0
                    flag = true
                }
            } catch (e: IOException) {
                Log.e(TAG, "receive: error while reading data", e)
                break
            }
        }
    }

    override fun onDestroy() {
        mBluetoothSocket?.close()
        mBluetoothServerSocket?.close()
        super.onDestroy()
    }

    private suspend fun setImage(bytes: ByteArray, byteLength: Int) {
        val bitmap = BitmapFactory.decodeByteArray(bytes, 0, byteLength)
        withContext(Dispatchers.Main) {
            mBinding.imageView.setImageBitmap(bitmap)
        }
    }

    companion object {
        private const val TAG = "ImageTransferPeripheral"
        val UUID_SERVICE: UUID = UUID.fromString("a9d158bb-9007-4fe3-b5d2-d3696a3eb067")

        fun createIntent(context: Context): Intent {
            return Intent(context, ImageTransferPeripheralActivity::class.java)
        }
    }
}