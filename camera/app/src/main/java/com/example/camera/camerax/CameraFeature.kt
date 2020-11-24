package com.example.camera.camerax

import android.content.Context
import android.net.Uri
import android.util.Log
import android.util.Size
import android.widget.Toast
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class CameraFeature {
    companion object {
        private const val TAG = "CameraFeature"
    }

    private var imageCapture: ImageCapture? = null

    fun startCamera(
        context: Context,
        lifecycleOwner: LifecycleOwner,
        viewFinder: PreviewView,
        executor: ExecutorService
    ) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

        cameraProviderFuture.addListener({
            // カメラのlifecycleを指定したlifecycle ownerとバインドするために使われる
            // ライフサイクルに対応させるため、カメラを開閉するタスクが不要になる
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            /**
             * ユースケース
             */
            // カメラプレビューのユースケース
            val preview = Preview.Builder().build()
                .also { it.setSurfaceProvider(viewFinder.surfaceProvider) }

            // カメラ撮影のユースケース
            imageCapture = ImageCapture.Builder().build()

            // 画像解析のユースケース
            val imageAnalysis  =ImageAnalysis.Builder()
                .setTargetResolution(Size(1280,720))
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .also {
                    it.setAnalyzer(executor, LuminosityAnalyzer())
                }

            /**
             * ユースケースをカメラにバインド
             */
            // 後方カメラをデフォルトに指定
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                // レンダリングする前にバインドを解除
                cameraProvider.unbindAll()

                // ユースケースをカメラにバインド
                cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    /*use cases...*/
                    preview,
                    imageCapture,
                    imageAnalysis
                )
            } catch (exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, /* メインスレッドで実行させる */ContextCompat.getMainExecutor(context))
    }

    fun takePhoto(
        context: Context,
        baseContext: Context,
        outputDirectory: File,
    ) {
        val imageCapture = imageCapture ?: return

        // 画像を保存するためのファイルを作成
        val photoFile = File(outputDirectory, "CameraTest.jpg")

        // ファイルとメタデータを持つ出力オプションを作成
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        // 写真が撮影されたときのリスナーを登録
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(context),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val savedUri = Uri.fromFile(photoFile)
                    val msg = "Photo capture succeeded: $savedUri"
                    Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, msg)
                }
            })
    }


}