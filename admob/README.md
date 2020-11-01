# AdMob
## 準備
AdMobにアプリを登録し、アプリIDを取得する

テストにはこのIDが利用可能: ca-app-pub-3940256099942544~3347511713

```xml
<manifest>
    <application>
        <meta-data
            android:name="com.google.android.gms.ads.APPLICATION_ID"
            android:value="ca-app-pub-xxxxxxxxxxxxxxxx~yyyyyyyyyy"/>
    </application>
</manifest>
```

```groovy
dependencies {
    implementation 'com.google.android.gms:play-services-ads:19.4.0'
}
```

### Mobile Ads SDKを初期化

広告を呼び出す前に`MobileAds.initialize()`で初期化する

アプリの起動時など１回だけ行う

```groovy
package ...
import ...
import com.google.android.gms.ads.MobileAds;

class MainActivity : AppCompatActivity() {
    ...
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        MobileAds.initialize(this) {}
    }
    ...
}
```
## バナー広告
テストID: ca-app-pub-3940256099942544/6300978111

### AdViewを追加

```xml
<com.google.android.gms.ads.AdView xmlns:ads="http://schemas.android.com/apk/res-auto"
    android:id="@+id/banner"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:layout_gravity="center"
    android:layout_marginBottom="8dp"
    ads:adSize="BANNER"
    ads:adUnitId="ca-app-pub-3940256099942544/6300978111" />
```

### 広告を読み込み

```kotlin
val adRequest = AdRequest.Builder().build()
binding.adView.loadAd(adRequest)
```

### アダプティブバナー
指定する広告の幅に応じて、自動的に最適な広告サイズが決まる

幅が同じなら、常に同じアスペクト比のバナーが返される

1. AdViewのコンテナとなるViewを用意する

    ```kotlin
    <FrameLayout
        android:id="@+id/ad_view_container"
        android:layout_width="match_parent"
        android:layout_height="wrap_content" />
    ```

    ```kotlin
    val adView = AdView(this)
    binding.adViewContainer.addView(adView)
    ```

2. アダプティブバナーの広告サイズを取得

    ```kotlin
    private val adSize: AdSize
        get() {
    				// 1. デバイス幅を取得
            val display = windowManager.defaultDisplay
            val outMetrics = DisplayMetrics()
            display.getMetrics(outMetrics)

            val density = outMetrics.density

            var adWidthPixels = binding.adViewContainer.width.toFloat()
            if (adWidthPixels == 0f) {
                adWidthPixels = outMetrics.widthPixels.toFloat()
            }

            val adWidth = (adWidthPixels / density).toInt()
    				// 広告サイズクラスの適切な静的メソッドを使用して、AdSizeを取得
            return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(this, adWidth)
        }
    ```

3. バナー広告ビューの**広告サイズ**と**広告ユニットID**を指定

    ```kotlin
    adView.adUnitId = "ca-app-pub-3940256099942544/6300978111"
    adView.adSize = adSize
    ```

4. 広告を読み込み

    ```kotlin
    val adRequest = AdRequest
        .Builder()
        .build()
    adView.loadAd(adRequest)
    ```
## インタースティシャル広告
### 準備

`InterstitialAd`のインスタンスを生成し予め広告を読み込んでおく

```kotlin
private lateinit var mInterstitialAd: InterstitialAd

mInterstitialAd = InterstitialAd(this)
mInterstitialAd.adUnitId = "ca-app-pub-3940256099942544/1033173712"

// 広告を読み込む
mInterstitialAd.loadAd(AdRequest.Builder().build())
```

### 表示

```kotlin
if (mInterstitialAd.isLoaded)
    mInterstitialAd.show()
else
    // インタースティシャル広告が読み込めていない
```
## リワード広告
### 準備

```kotlin
private lateinit var rewardedAd: RewardedAd

// 広告を読み込み
rewardedAd = RewardedAd(this, "ca-app-pub-3940256099942544/5224354917")
rewardedAd.loadAd(AdRequest.Builder().build(), adLoadCallback)
```

```kotlin
// 広告を読み込むときのコールバックオブジェクト
private val adLoadCallback = object : RewardedAdLoadCallback() {
    // 読み込みに成功
    override fun onRewardedAdLoaded() {
        Log.d(TAG, "onRewardedAdLoaded")
    }

    // 読み込み失敗
    override fun onRewardedAdFailedToLoad(adError: LoadAdError) {
        Log.d(TAG, "onRewardedAdFailedToLoad")
    }
}
```

### 表示

```kotlin
if (rewardedAd.isLoaded)
    rewardedAd.show(this, adCallback)
else
    // 広告を読み込めていない
```

```kotlin
// 広告を表示するときのコールバックオブジェクト
private val adCallback = object : RewardedAdCallback() {
    override fun onRewardedAdOpened() {
        // 広告がデバイスの画面いっぱいに表示されると呼ばれる
        this@RewardActivity.loadRewardAd()
    }

    override fun onRewardedAdClosed() {
        // ユーザーが閉じる、戻るボタンをタップして閉じたとき呼ばれる
    }

    override fun onUserEarnedReward(reward: RewardItem) {
        // ユーザーが報酬を獲得
    }

    override fun onRewardedAdFailedToShow(adError: AdError) {
        // 表示に失敗
    }
}
```

## ネイティブ広告

広告の読み込み時に広告に関する`NativeAd`オブジェクトを受け取って、

その内容を自分で画面に表示する

### UnifiedNativeAdView

`UnifiedNativeAdView`の中に表示したい広告のデータに対応するViewを配置する

```kotlin
<com.google.android.gms.ads.formats.UnifiedNativeAdView xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="wrap_content">

	  <ImageView
            android:id="@+id/ad_app_icon"/>
		...
	
</com.google.android.gms.ads.formats.UnifiedNativeAdView>
```

### AdLoader

```kotlin
private val nativeAdOptions = NativeAdOptions.Builder().build()

// ネイティブ広告を読み込むためのクラス
// ネイティブ広告の種類などを指定できる
val adLoader = AdLoader.Builder(this, "ca-app-pub-3940256099942544/2247696110")
    .forUnifiedNativeAd { ad: UnifiedNativeAd ->
        // 統合型ネイティブ広告をリクエストするように設定
        // 読み込みに成功すると onUnifiedNativeAdLoaded() が呼び出される
        showUnifiedNativeAd(ad)
    }
    .withAdListener(adListener)
    .withNativeAdOptions(nativeAdOptions)
    .build()
```

### 広告の読み込み

```kotlin
adLoader.loadAd(AdRequest.Builder().build())
```

最大５個の広告に対してリクエストを送信できる

```kotlin
adLoader.loadAds(AdRequest.Builder().build(), 5)
```

### 表示

- `forUnifiedNativeAd`から`UnifiedNativeAd`を受け取る
- Viewから`UnifiedNativeAdView`を取得
- `UnifiedNativeAdView`に`UnifiedNativeAd`をセットする

```kotlin
// UnifiedNativeAdViewを取得
val adView = binding.root

// 広告の情報をViewに反映
binding.adHeadline.text = ad.headline
// UnifiedNativeAdViewに登録
adView.headlineView = binding.adHeadline

// NativeAdObjectを登録
adView.setNativeAd(ad)
```

```kotlin
// 広告の見出しのテキストを設定
// UnifiedNativeAdViewに登録
binding.adHeadline.text = ad.headline
adView.headlineView = binding.adHeadline

// アイコン
binding.adAppIcon.setImageDrawable(ad.icon.drawable)
adView.iconView = binding.adAppIcon

// MediaViewの登録
// 動画や画像を表示するためのView
binding.adMediaView.setMediaContent(ad.mediaContent)
adView.mediaView = binding.adMediaView

// 星
binding.star.text = "STAR: ${ad.starRating}"
adView.starRatingView = binding.star

// 値段
binding.price.text = "PRICE: ${ad.price}"
adView.priceView = binding.price
```

### 広告のミュート機能

流れ

- ボタンを押すなどして広告をミュートする
- ミュートした理由についての選択肢を表示
- 選択された理由をレポートする

広告をミュートできるようにする

```kotlin
private val nativeAdOptions = NativeAdOptions.Builder()
        .setRequestCustomMuteThisAd(true)
        .build()

val adLoader = AdLoader.Builder(this, "ca-app-pub-3940256099942544/2247696110")
		// ...
    .withNativeAdOptions(nativeAdOptions)
    .build()
```

選択肢を表示し、広告をミュート

```kotlin
if (ad.isCustomMuteThisAdEnabled) {
    // 広告を非表示にする理由を表示して選択させる
    val reasons = ad.muteThisAdReasons

    // 非表示の理由をレポートする
    val reason = reasons.first()
    ad.muteThisAd(reason)
} else {
    // この広告をミュートにすることはできないので
    // ビューを隠すなどの処理をする
}
```