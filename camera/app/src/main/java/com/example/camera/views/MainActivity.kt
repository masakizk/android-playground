package com.example.camera.views

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.camera.CameraFeature
import com.example.camera.R
import com.example.camera.databinding.ActivityMainBinding
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {

    private lateinit var cameraFeature: CameraFeature

    private lateinit var outputDirectory: File
    private lateinit var cameraExecutor: ExecutorService

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        outputDirectory = getOutputDirectory()
        cameraExecutor = Executors.newSingleThreadExecutor()
        cameraFeature = CameraFeature(binding.viewFinder, this, cameraExecutor)

        binding.cameraCaptureButton.setOnClickListener { takePhoto() }
        binding.turnCamera.setOnClickListener { cameraFeature.switchCamera() }
        binding.photoPreviewButton.setOnClickListener {
            if (outputDirectory.listFiles()?.isNotEmpty() == true) {

            }
        }


        // カメラの許可をもらう
        if (isPermissionGranted()) startCamera()
        else ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.CAMERA),
            REQUEST_CODE_PERMISSIONS
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (isPermissionGranted()) {
                startCamera()
            } else {
                Toast.makeText(this, "許可が与えられていません.", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun startCamera() {
        cameraFeature.startCamera(this)
    }

    private fun takePhoto() {
        cameraFeature.takePhoto(this, baseContext, outputDirectory)
    }

    private fun isPermissionGranted(): Boolean {
        val permission = ContextCompat.checkSelfPermission(
            baseContext,
            Manifest.permission.CAMERA
        )
        return permission == PackageManager.PERMISSION_GRANTED
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun getOutputDirectory(): File {
        val mediaDir = externalMediaDirs.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply { mkdirs() }
        }
        return if (mediaDir != null && mediaDir.exists())
            mediaDir else filesDir
    }

    companion object {
        private const val REQUEST_CODE_PERMISSIONS = 10
    }
}