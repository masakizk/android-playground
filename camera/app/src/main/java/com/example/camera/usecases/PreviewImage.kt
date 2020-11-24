package com.example.camera.usecases

import androidx.camera.core.Preview
import androidx.camera.view.PreviewView

object PreviewImage {
    // カメラプレビューのユースケース
    fun useCase(viewFinder: PreviewView): Preview {
        return Preview.Builder().build().also {
            it.setSurfaceProvider(viewFinder.surfaceProvider)
        }
    }
}