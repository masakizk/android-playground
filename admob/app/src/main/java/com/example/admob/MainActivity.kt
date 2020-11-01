package com.example.admob

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.admob.databinding.ActivityMainBinding
import com.google.android.gms.ads.MobileAds

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Mobile Ads SDK
        MobileAds.initialize(this) {}

        binding.apply {
            banner.setOnClickListener {
                startActivity(Intent(applicationContext, BannerActivity::class.java))
            }
            interstitial.setOnClickListener {
                startActivity(Intent(applicationContext, InterstitialActivity::class.java))
            }
            reward.setOnClickListener {
                startActivity(Intent(applicationContext, RewardActivity::class.java))
            }
            nativeAd.setOnClickListener {
                startActivity(Intent(applicationContext, NativeAdActivity::class.java))
            }

        }
    }


}