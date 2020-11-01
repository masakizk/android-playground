package com.example.admob

import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import com.example.admob.databinding.ActivityNativeAdBinding
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.formats.NativeAdOptions
import com.google.android.gms.ads.formats.UnifiedNativeAd
import com.google.android.gms.ads.formats.UnifiedNativeAdView

class NativeAdActivity : Activity() {
    private lateinit var binding: ActivityNativeAdBinding

    // AdLoader
    // ネイティブ広告を読み込むためにAdLoaderクラスを使用
    // ネイティブ広告の種類を指定することが可能
    // setRequestCustomMuteThisAd: 広告非表示機能を有効にする
    private val nativeAdOptions = NativeAdOptions.Builder()
        .setRequestCustomMuteThisAd(true)
        .build()

    private val adListener = object : AdListener() {
        override fun onAdFailedToLoad(errorCode: Int) {
            // 広告の読み込みに失敗
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNativeAdBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val adLoader = AdLoader.Builder(this, "ca-app-pub-3940256099942544/2247696110")
            .forUnifiedNativeAd { ad: UnifiedNativeAd ->
                // 統合型ネイティブ広告をリクエストするように設定
                // 読み込みに成功すると onUnifiedNativeAdLoaded() が呼び出される
                showUnifiedNativeAd(ad)
            }
            .withAdListener(adListener)
            .withNativeAdOptions(nativeAdOptions)
            .build()

        // 広告の読み込み
        adLoader.loadAd(AdRequest.Builder().build())

        // 最大５個の広告に対してリクエストを送信できる
        // adLoader.loadAds(AdRequest.Builder().build(), 5)

    }

    private fun showUnifiedNativeAd(ad: UnifiedNativeAd) {
        val adView = binding.root

        showAdInformation(adView, ad)

        // NativeAdObjectを登録
        adView.setNativeAd(ad)

        // 広告をミュート
        binding.muteThisAd.setOnClickListener {
            if (ad.isCustomMuteThisAdEnabled) {
                // 広告を非表示にする理由を表示して選択させる
                val reasons = ad.muteThisAdReasons
                Toast.makeText(applicationContext, "REASONS: $reasons", Toast.LENGTH_LONG).show()

                // 非表示の理由をレポートする
                // val reason = reasons.first()
                // ad.muteThisAd(reason)
            } else {
                // この広告をミュートにすることはできないので
                // ビューを隠すなどの処理をする
                Toast.makeText(applicationContext, "HIDE ADS", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun showAdInformation(adView: UnifiedNativeAdView, ad: UnifiedNativeAd) {
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
    }

    companion object {
        private const val TAG = "NativeAdActivity"
    }
}