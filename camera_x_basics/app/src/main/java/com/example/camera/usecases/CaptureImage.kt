package com.example.camera.usecases

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import com.example.camera.ScreenAspectRatio
import java.io.File

object CaptureImage {

    // 撮影のユースケース
    fun useCase(viewFinder: PreviewView): ImageCapture {
        val aspectRatio = ScreenAspectRatio(viewFinder)
        val rotation = viewFinder.display.rotation

        return ImageCapture.Builder()
            // 撮影する速さを優先(画質が下がる可能性がある)
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
            // CameraXがユースケースに最適な解像度にする
            .setTargetAspectRatio(aspectRatio)
            // ユースケースのライフサイクル中に rotation が変更された場合、再度呼び出す必要がある
            .setTargetRotation(rotation)
            .build()
    }

    fun takePicture(
        context: Context,
        baseContext: Context,
        outputDirectory: File,
        imageCapture: ImageCapture
    ){
        // 画像を保存するためのファイルを作成
        val photoFile = File(outputDirectory, "CameraTest.jpg")

        // ファイルとメタデータを持つ出力オプションを作成
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        // 写真が撮影されたときのリスナーを登録
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(context),
            ImageCaptureCallbackListener(baseContext, photoFile)
        )
    }
}


class ImageCaptureCallbackListener(
    private val baseContext: Context,
    private val photoFile: File
) : ImageCapture.OnImageSavedCallback {
    override fun onError(exc: ImageCaptureException) {
        Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
    }

    override fun onImageSaved(output: ImageCapture.OutputFileResults) {
        val savedUri = Uri.fromFile(photoFile)
        val msg = "Photo capture succeeded: $savedUri"
        Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
        Log.d(TAG, msg)
    }

    companion object {
        private const val TAG = "CameraFeature"
    }
}