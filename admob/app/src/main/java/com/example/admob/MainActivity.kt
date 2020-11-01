package com.example.admob

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.admob.databinding.ActivityMainBinding
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        MobileAds.initialize(this) {}

        binding.apply {
            loadAd(banner)
            loadAd(largeBanner)
            loadAd(mediumRectangle)
            loadAd(fullBanner)
            loadAd(leaderBoard)
            loadAd(smartBanner)

            interstitial.setOnClickListener {
                startActivity(Intent(applicationContext, InterstitialActivity::class.java))
            }
        }
    }

    private fun loadAd(adView: AdView){
        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)
    }
}