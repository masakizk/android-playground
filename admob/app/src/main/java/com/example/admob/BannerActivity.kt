package com.example.admob

import android.app.Activity
import android.os.Bundle
import android.util.DisplayMetrics
import com.example.admob.databinding.ActivityBannerBinding
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView

class BannerActivity : Activity() {
    private lateinit var binding: ActivityBannerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBannerBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.apply {
            loadAd(banner)
            loadAd(largeBanner)
            loadAd(mediumRectangle)
            loadAd(fullBanner)
            loadAd(leaderBoard)
            loadAd(smartBanner)
            setupAdaptiveBanner()
        }
    }

    private fun loadAd(adView: AdView) {
        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)
    }

    private val adSize: AdSize
        get() {
            val display = windowManager.defaultDisplay
            val outMetrics = DisplayMetrics()
            display.getMetrics(outMetrics)

            val density = outMetrics.density

            var adWidthPixels = binding.adViewContainer.width.toFloat()
            if (adWidthPixels == 0f) {
                adWidthPixels = outMetrics.widthPixels.toFloat()
            }

            val adWidth = (adWidthPixels / density).toInt()
            return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(this, adWidth)
        }

    private fun setupAdaptiveBanner() {
        // FrameLayoutにAdViewをセット
        val adView = AdView(this)
        binding.adViewContainer.addView(adView)
        // AdViewの設定
        adView.adUnitId = "ca-app-pub-3940256099942544/6300978111"
        adView.adSize = adSize

        val adRequest = AdRequest
            .Builder()
            .build()
        adView.loadAd(adRequest)
    }
}