package com.example.admob

import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import com.example.admob.databinding.ActivityInterstitialBinding
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import com.google.android.gms.ads.MobileAds

class InterstitialActivity : Activity() {
    private lateinit var binding: ActivityInterstitialBinding

    private lateinit var mInterstitialAd: InterstitialAd

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInterstitialBinding.inflate(layoutInflater)
        setContentView(binding.root)

        MobileAds.initialize(this) {}

        mInterstitialAd = InterstitialAd(this)
        mInterstitialAd.adUnitId = "ca-app-pub-3940256099942544/1033173712"
        // 広告を読み込む
        mInterstitialAd.loadAd(AdRequest.Builder().build())

        binding.showInterstitial.setOnClickListener {
            showInterstitial()
        }
    }

    private fun showInterstitial() {
        if (mInterstitialAd.isLoaded)
            mInterstitialAd.show()
        else
            Toast.makeText(this, "The interstitial wasn't loaded yet.", Toast.LENGTH_LONG).show()
    }



}