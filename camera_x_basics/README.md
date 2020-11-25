# CameraX
- [準備](#準備)
  * [MainActivity](#mainactivity)
  * [大まかな流れ](#大まかな流れ)
- [Permission](#permission)
- [カメラを開始](#カメラを開始)
- [ユースケース](#ユースケース)
  * [プレビュー](#プレビュー)
  * [画像キャプチャ](#画像キャプチャ)
    + [撮影](#撮影)
    + [画像保存先のファイルを作成](#画像保存先のファイルを作成)
  * [画像解析](#画像解析)

<small><i><a href='http://ecotrust-canada.github.io/markdown-toc/'>Table of contents generated with markdown-toc</a></i></small>

## 準備
build.gradle

```groovy
apply plugin: 'kotlin-android-extensions'

android {
    defaultConfig {
        minSdkVersion 21
    }

    compileOptions {
        sourceCompatibility JavaVersion.VERSION_1_8
        targetCompatibility JavaVersion.VERSION_1_8
    }
}

dependencies {
    def camerax_version = "1.0.0-beta11"
    // CameraX core library using camera2 implementation
    implementation "androidx.camera:camera-camera2:$camerax_version"
    // CameraX Lifecycle Library
    implementation "androidx.camera:camera-lifecycle:$camerax_version"
    // CameraX View class
    implementation "androidx.camera:camera-view:1.0.0-alpha18"
}
```

AndroidManifest

```kotlin
<uses-feature android:name="android.hardware.camera.any" />
<uses-permission android:name="android.permission.CAMERA" />
```
- ### MainActivity
  `PreviewView`を追加
  ```xml
  <androidx.camera.view.PreviewView
        android:id="@+id/viewFinder"
        android:layout_width="match_parent"
        android:layout_height="match_parent" />
  ```

### 大まかな流れ

1. Preview Viewをレイアウトに追加

    ```xml
    <androidx.camera.view.PreviewView
            android:id="@+id/viewFinder"
            android:layout_width="match_parent"
            android:layout_height="match_parent" />
    ```

2. カメラの許可をもらう

    ```kotlin
    if (isPermissionGranted()) /* カメラを開始 */
    else /* 許可をリクエスト */
    ```

3. 出力先やカメラの実行スレッドを用意

    ```kotlin
    /* 画像の出力先のディレクトリを取得 */
    outputDirectory = getOutputDirectory()
    cameraExecutor = Executors.newSingleThreadExecutor()
    ```

    ```kotlin
    // アプリが閉じるときにスレッドを開放する
    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }
    ```

4. カメラのライフサイクルを結び付ける

    ```kotlin
    /* CameraProviderを取得し、アプリのライフサイクルと結び付ける */
    val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
    val cameraProvider = cameraProviderFuture.get()
    ```

5. ユースケースをカメラにバインド

    `Preview`, `ImageCapture`などのユースケースをバインドすることでカメラの機能を拡張できる

    ```kotlin
    /* レンダリングする前にバインドを解除　*/
    cameraProvider.unbindAll()
    try {
        /* ユースケースをカメラにバインド */
        cameraProvider.bindToLifecycle(
            lifecycleOwner, cameraSelector,
            /*use cases...*/preview, imageCapture, imageAnalysis
        )
        preview?.setSurfaceProvider(viewFinder.surfaceProvider)
    } catch (exc: Exception) {}
    ```
    
## Permission
```kotlin
Manifest.permission.CAMERA
```
### 許可をリクエストする

```kotlin
ActivityCompat.requestPermissions(
    /*activity*/this,
    /*permission*/arrayOf(**Manifest.permission.CAMERA**),
    /*request code*/**REQUEST_CODE_PERMISSIONS**
)
```

### 結果を受け取る

```kotlin
// パーミッションの結果が出たら呼ばれる
override fun onRequestPermissionsResult(requestCode: Int,permissions: Array<String>,grantResults: IntArray) {
    if (requestCode == **REQUEST_CODE_PERMISSIONS**) {
        if (isPermissionGranted()) // 許可を得られた(カメラを開始)
        else // 許可が与えられなかった
    }
}
```

### 認められたかを確認

```kotlin
private fun isPermissionGranted(): Boolean {
    val permission = ContextCompat.checkSelfPermission(
        baseContext,
        Manifest.permission.CAMERA
    )
    return permission == PackageManager.PERMISSION_GRANTED
}
```

## カメラを開始
```kotlin
val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

cameraProviderFuture.addListener({
    // camera providerを取得
    val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

    /* カメラのユースケースを作成　*/

    // レンダリングする前にバインドを解除
    cameraProvider.unbindAll()

    // カメラの向きを指定
    val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

    try {
        // ユースケースをカメラにバインド
        cameraProvider.bindToLifecycle(
			lifecycleOwner,　cameraSelector,
			/*use cases...*/ imageCapture
        )
    } catch (exc: Exception) {}

}, /* メインスレッドで実行させる */ContextCompat.getMainExecutor(context))
```

## ユースケース
### プレビュー
```kotlin
Preview.Builder().build().also {
    it.setSurfaceProvider(viewFinder.surfaceProvider)
}
```
### 画像キャプチャ
```kotlin
val imageCapture = ImageCapture.Builder().build()
```
#### 撮影

```kotlin
// 出力先となるディレクトリを取得
val outputDirectory = getOutputDirectory()
// 出力するファイルを作成
val photoFile = File(outputDirectory, "CameraTest.jpg")

// 出力オプション: 保存場所、メタデータを持つ
val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

// 写真が撮影されたときのリスナーを登録
imageCapture.takePicture(
    outputOptions,
    ContextCompat.getMainExecutor(context),
    object : ImageCapture.OnImageSavedCallback{...}
)
```

#### 画像保存先のファイルを作成

```kotlin
@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
private fun getOutputDirectory(): File {
    val mediaDir = externalMediaDirs.firstOrNull()?.let {
        File(it, resources.getString(R.string.app_name)).apply { mkdirs() }
    }
    return if (mediaDir != null && mediaDir.exists())
        mediaDir else filesDir
}
```

### 画像解析
```kotlin
ImageAnalysis.Builder()
  .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
  .build()
  .also {
      it.setAnalyzer(executor, SomeAnalyzer())
  }
```
```kotlin
class SomeAnalyzer: ImageAnalysis.Analyzer {
    override fun analyze(image: ImageProxy) {
				println("${image.height}x${image.width}")
        image.close()
    }
}
```