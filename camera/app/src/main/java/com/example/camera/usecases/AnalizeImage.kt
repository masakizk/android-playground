package com.example.camera.usecases

import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.view.PreviewView
import com.example.camera.ScreenAspectRatio
import java.nio.ByteBuffer
import java.util.concurrent.ExecutorService

object AnaliseImage {
    // 画像解析のユースケース
    fun useCase(viewFinder: PreviewView, executor: ExecutorService): ImageAnalysis {
        val aspectRatio = ScreenAspectRatio(viewFinder)
        val rotation = viewFinder.display.rotation

        return ImageAnalysis.Builder()
            // 画像改造とを指定
            .setTargetAspectRatio(aspectRatio)
            .setTargetRotation(rotation)
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()
            .also {
                it.setAnalyzer(executor, LuminosityAnalyzer())
            }
    }
}

class LuminosityAnalyzer: ImageAnalysis.Analyzer {

    private fun ByteBuffer.toByteArray(): ByteArray {
        rewind()    // Rewind the buffer to zero
        val data = ByteArray(remaining())
        get(data)   // Copy the buffer into a byte array
        return data // Return the byte array
    }

    override fun analyze(image: ImageProxy) {
        val buffer = image.planes[0].buffer
        val data = buffer.toByteArray()
        val pixels = data.map { it.toInt() and 0xFF }
        val luma = pixels.average()

        Log.d(TAG, "Average luminosity: $luma")
        image.close()
    }

    companion object{
        private const val TAG = "LuminosityAnalyzer"
    }
}