# CameraX
## 準備
- ### build.gradle(app)
  ```groovy
  plugins {
      /* ... */ 
      id 'kotlin-android-extensions'
  }

  android {
      /* ... */
      defaultConfig {
          minSdkVersion 21
      }
      
      /* ... */
      compileOptions {
          sourceCompatibility JavaVersion.VERSION_1_8
          targetCompatibility JavaVersion.VERSION_1_8
      }
  }

  dependencies {
      /* ... */

      def camerax_version = "1.0.0-beta11"
      // CameraX core library using camera2 implementation
      implementation "androidx.camera:camera-camera2:$camerax_version"
      // CameraX Lifecycle Library
      implementation "androidx.camera:camera-lifecycle:$camerax_version"
      // CameraX View class
      implementation "androidx.camera:camera-view:1.0.0-alpha18"
  }
  ```
- ### AndroidManifest
  ```xml
  <uses-feature android:name="android.hardware.camera.any" />
  <uses-permission android:name="android.permission.CAMERA" />
  <application
  ```
- ### MainActivity
  `PreviewView`を追加
  ```xml
  <androidx.camera.view.PreviewView
        android:id="@+id/viewFinder"
        android:layout_width="match_parent"
        android:layout_height="match_parent" />
  ```
## Permission
カメラの許可

```kotlin
Manifest.permission.CAMERA
```

### 確認

```kotlin
val permission = ContextCompat.checkSelfPermission(
    baseContext,
		/* 確認するpermission */
    Manifest.**permission.CAMERA**
)

return permission == PackageManager.**PERMISSION_GRANTED**
```

### 許可をリクエストする

```kotlin
ActivityCompat.requestPermissions(
    /*activity*/this,
    /*permission*/arrayOf(**Manifest.permission.CAMERA**),
    /*request code*/**REQUEST_CODE_PERMISSIONS**
)
```

```kotlin
// パーミッションの結果が出たら呼ばれる
override fun onRequestPermissionsResult(
    requestCode: Int,
    permissions: Array<String>,
    grantResults: IntArray
) {
    if (requestCode == **REQUEST_CODE_PERMISSIONS**) {
        if (isPermissionGranted()) {
            startCamera()
        } else {
            Toast.makeText(this, "許可が与えられていません.", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}
```
## カメラ利用の概要
カメラを利用するときは`Preview`, `ImageCapture`などのユースケースをカメラにバインドする
```kotlin
// カメラプレビューのユースケース
val preview = Preview.Builder().build()
// カメラ撮影のユースケース
imageCapture = ImageCapture.Builder().build()

cameraProvider.bindToLifecycle(
    lifecycleOwner,
    cameraSelector,
    /*use cases...*/
    preview,
    imageCapture
)
```
## プレビュー
```kotlin
val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

cameraProviderFuture.addListener({
    // カメラのlifecycleを指定したlifecycle ownerとバインドするために使われる
    // ライフサイクルに対応させるため、カメラを開閉するタスクが不要になる
    val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

    // カメラプレビューのユースケース
    val preview = Preview.Builder().build()
        .also {
            // プレビューをPreviewViewにバインド
            it.setSurfaceProvider(viewFinder.surfaceProvider) 
        }

    // 後方カメラをデフォルトに指定
    val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

    // レンダリングする前にユースケースを解除
    cameraProvider.unbindAll()

    // ユースケースをカメラにバインド
    cameraProvider.bindToLifecycle(
        lifecycleOwner,
        cameraSelector,
        /*use cases...*/
        preview,
    )

}, /* メインスレッドで実行させる */ContextCompat.getMainExecutor(context))
```
## 撮影
```kotlin
// 画像を保存するためのファイルを作成
val photoFile = File(outputDirectory, "CameraTest.jpg")
```

```kotlin
// ファイルとメタデータを持つ出力オプションを作成
val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()
```

```kotlin
// 写真を撮影
imageCapture.takePicture(
    /* 出力オプション */ outputOptions,
    /* 実行スレッド   */ ContextCompat.getMainExecutor(context),
    /* リスナー       */ object : ImageCapture.OnImageSavedCallback {

        override fun onError(exc: ImageCaptureException) {
            Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
        }

        override fun onImageSaved(output: ImageCapture.OutputFileResults) {
            val savedUri = Uri.fromFile(photoFile)
            val msg = "Photo capture succeeded: $savedUri"
        }
    })
```
プレビュー起動時
```kotlin
imageCapture = ImageCapture.Builder().build()

cameraProvider.bindToLifecycle(
    lifecycleOwner,
    cameraSelector,
    /*use cases...*/
    preview,
    imageCapture
)
```