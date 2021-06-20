package com.example.android.bluetoothconnection.socket.image

import android.annotation.SuppressLint
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.util.*
import kotlin.math.min

class BitmapImageConverter(
    context: Context
) : ImageAnalysis.Analyzer {

    private var mSocket: BluetoothSocket? = null

    fun setSocket(socket: BluetoothSocket) {
        Log.d(TAG, "setSocket: done")
        mSocket = socket
    }

    private val mConverter = YuvToRgbConverter(context)
    private val mCoroutineScope = CoroutineScope(Dispatchers.IO)

    @SuppressLint("UnsafeOptInUsageError")
    override fun analyze(image: ImageProxy) {
        Log.d(TAG, "analyze: before ${image.image == null} ${mSocket == null}")

        val mediaImage = image.image
        val socket = mSocket
        if (mediaImage == null || socket == null) {
            image.close()
            return
        }

        mCoroutineScope.launch(Dispatchers.IO) {
            var bitmap = Bitmap.createBitmap(image.width, image.height, Bitmap.Config.ARGB_8888)
            mConverter.yuvToRgb(mediaImage, bitmap)
            bitmap = bitmap.rotate(90f)
            val stream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 10, stream)

            val bytes = stream.toByteArray()
            val subBytesLength = 400

            socket.outputStream.write("${bytes.size}".toByteArray())
            socket.outputStream.flush()

            for (i in 0..bytes.size step subBytesLength) {
                val tmp = Arrays.copyOfRange(bytes, i, min(bytes.size, i + subBytesLength))
                socket.outputStream.write(tmp)
                socket.outputStream.flush()
            }
        }.invokeOnCompletion {
            image.close()
        }
    }

    private fun Bitmap.rotate(degrees: Float): Bitmap {
        val matrix = Matrix()
        matrix.setRotate(degrees, width / 2f, height / 2f)
        return Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
    }

    companion object {
        private const val TAG = "BitmapImageConverter"
    }
}