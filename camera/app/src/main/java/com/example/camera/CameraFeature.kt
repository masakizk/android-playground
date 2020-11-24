package com.example.camera

import android.content.Context
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.example.camera.usecases.AnaliseImage
import com.example.camera.usecases.CaptureImage
import com.example.camera.usecases.PreviewImage
import java.io.File
import java.util.concurrent.ExecutorService

class CameraFeature(
    private val viewFinder: PreviewView,
    private val lifecycleOwner: LifecycleOwner,
    private val cameraExecutor: ExecutorService
) {
    // Use Cases
    private var preview: Preview? = null
    private var imageCapture: ImageCapture? = null
    private var imageAnalysis: ImageAnalysis? = null

    private var lensFacing: Int = CameraSelector.LENS_FACING_BACK

    private lateinit var cameraProvider: ProcessCameraProvider

    fun startCamera(
        context: Context,
    ) {
        // カメラのライフサイクルをアプリのライフサイクルに結び付ける
        // カメラを開閉するタスクが不要になる
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

        cameraProviderFuture.addListener({
            cameraProvider = cameraProviderFuture.get()
            bindCameraUseCases()
        }, /* メインスレッドで実行させる */ContextCompat.getMainExecutor(context))
    }

    private fun bindCameraUseCases() {
        preview = PreviewImage.useCase(viewFinder)
        imageCapture = CaptureImage.useCase(viewFinder)
        imageAnalysis = AnaliseImage.useCase(viewFinder, cameraExecutor)

        // 後方カメラをデフォルトに指定
        val cameraSelector = CameraSelector.Builder().requireLensFacing(lensFacing).build()

        // レンダリングする前にバインドを解除
        cameraProvider.unbindAll()

        try {
            // ユースケースをカメラにバインド
            cameraProvider.bindToLifecycle(
                lifecycleOwner, cameraSelector,
                /*use cases...*/preview, imageCapture, imageAnalysis
            )

            preview?.setSurfaceProvider(viewFinder.surfaceProvider)
        } catch (exc: Exception) {
            Log.e(TAG, "Use case binding failed", exc)
        }
    }

    fun takePhoto(context: Context, baseContext: Context, outputDirectory: File) {
        CaptureImage.takePicture(context, baseContext, outputDirectory, imageCapture!!)
    }

    // カメラの向きを切り替え
    fun switchCamera() {
        lensFacing = if (CameraSelector.LENS_FACING_FRONT == lensFacing) {
            CameraSelector.LENS_FACING_BACK
        } else {
            CameraSelector.LENS_FACING_FRONT
        }
        bindCameraUseCases()
    }

    companion object {
        private const val TAG = "CameraFeature"
    }
}