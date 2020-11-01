package com.example.admob

import android.app.Activity
import android.os.Bundle
import com.example.admob.databinding.ActivityBannerBinding
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds

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
        }
    }

    private fun loadAd(adView: AdView) {
        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)
    }
}