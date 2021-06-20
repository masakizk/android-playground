package com.example.android.bluetoothconnection.socket.image

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.lifecycleScope
import com.example.android.bluetoothconnection.R
import com.example.android.bluetoothconnection.databinding.ActivityImageCentralBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.math.min

class ImageTransferCentralActivity : AppCompatActivity() {
    private lateinit var mBinding: ActivityImageCentralBinding
    private val mDeviceName: String get() = intent.getStringExtra(PARAM_DEVICE_NAME)!!
    private val mDeviceAddress: String get() = intent.getStringExtra(PARAM_DEVICE_ADDRESS)!!

    private lateinit var mBluetoothAdapter: BluetoothAdapter
    private var mBluetoothSocket: BluetoothSocket? = null

    private var mBitmapConverter: BitmapImageConverter? = null

    private lateinit var cameraExecutor: ExecutorService

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        mBinding = ActivityImageCentralBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        cameraExecutor = Executors.newSingleThreadExecutor()

        mBinding.apply {
            textDeviceAddress.text = mDeviceAddress
            textDeviceName.text = mDeviceName

            buttonConnect.setOnClickListener { lifecycleScope.launch { connect() } }
            buttonDisconnect.setOnClickListener { disconnect() }
            buttonSend.setOnClickListener { lifecycleScope.launch { send() } }
        }

        val cameraPermission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
        if (cameraPermission) startCamera()
        else requestPermissions(
            arrayOf(Manifest.permission.CAMERA),
            100
        )
    }

    override fun onDestroy() {
        mBluetoothSocket?.close()
        super.onDestroy()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            100 -> startCamera()
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

        val preview = Preview.Builder()
            .build()

        val selector = CameraSelector.Builder()
            .requireLensFacing(CameraSelector.LENS_FACING_BACK)
            .build()

        val imageAnalyzer = ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()
            .apply {
                val analyzer = BitmapImageConverter(this@ImageTransferCentralActivity)
                mBitmapConverter = analyzer
                setAnalyzer(cameraExecutor, analyzer)
            }

        cameraProviderFuture.addListener({
            kotlin.runCatching {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                    this,
                    selector,
                    preview,
                    imageAnalyzer
                )

                preview.setSurfaceProvider(mBinding.viewFinder.surfaceProvider)
            }.onFailure { e ->
                Log.e(TAG, "Use case binding failed", e)
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private suspend fun connect() = withContext(Dispatchers.IO) {
        val device = mBluetoothAdapter.getRemoteDevice(mDeviceAddress)
        if (device == null) {
            withContext(Dispatchers.Main) {
                Toast.makeText(
                    this@ImageTransferCentralActivity,
                    "cannot find",
                    Toast.LENGTH_LONG
                ).show()
            }
            return@withContext
        }

        val serviceUUID = ImageTransferPeripheralActivity.UUID_SERVICE
        mBluetoothSocket = device.createRfcommSocketToServiceRecord(serviceUUID)

        if (mBluetoothSocket == null) return@withContext

        kotlin.runCatching {
            mBluetoothAdapter.cancelDiscovery()
            mBluetoothSocket!!.connect()
        }.onSuccess {
            withContext(Dispatchers.Main) { connected() }
            mBitmapConverter?.setSocket(mBluetoothSocket!!)
        }.onFailure {
            withContext(Dispatchers.Main) {
                Toast.makeText(
                    this@ImageTransferCentralActivity,
                    "failed in connect",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun connected() {
        mBinding.apply {
            buttonConnect.isEnabled = false
            buttonDisconnect.isEnabled = true
            buttonSend.isEnabled = true
        }
    }

    private fun disconnect() {
        mBluetoothSocket?.close()
    }

    private suspend fun send() = withContext(Dispatchers.IO) {
        val socket = mBluetoothSocket ?: return@withContext
        val bitmap = ContextCompat.getDrawable(
            this@ImageTransferCentralActivity,
            R.drawable.logo
        )?.toBitmap()!!
        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 0, stream)

        val bytes = stream.toByteArray()
        val subBytesLength = 400

        socket.outputStream.write("${bytes.size}".toByteArray())
        socket.outputStream.flush()

        for (i in 0..bytes.size step subBytesLength) {
            val tmp = Arrays.copyOfRange(bytes, i, min(bytes.size, i + subBytesLength))
            socket.outputStream.write(tmp)
            socket.outputStream.flush()
        }
    }

    companion object {
        private const val TAG = "ImageTransferCentralAct"

        private const val PARAM_DEVICE_NAME = "NAME"
        private const val PARAM_DEVICE_ADDRESS = "ADDRESS"

        fun createIntent(context: Context, deviceName: String, deviceAddress: String): Intent {
            return Intent(context, ImageTransferCentralActivity::class.java).apply {
                putExtra(PARAM_DEVICE_NAME, deviceName)
                putExtra(PARAM_DEVICE_ADDRESS, deviceAddress)
            }
        }
    }
}