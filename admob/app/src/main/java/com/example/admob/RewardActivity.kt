package com.example.admob

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.admob.databinding.ActivityRewardBinding
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.rewarded.RewardItem
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdCallback
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback

class RewardActivity : Activity() {
    private lateinit var binding: ActivityRewardBinding
    private lateinit var rewardedAd: RewardedAd

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRewardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadRewardAd()

        binding.showRewardAd.setOnClickListener {
            showRewardAd()
        }
    }

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

    // 広告を読み込む
    private fun loadRewardAd() {
        rewardedAd = RewardedAd(this, "ca-app-pub-3940256099942544/5224354917")
        rewardedAd.loadAd(AdRequest.Builder().build(), adLoadCallback)
    }

    // 広告を表示する
    private fun showRewardAd() {
        if (rewardedAd.isLoaded)
            rewardedAd.show(this, adCallback)
        else
            Toast.makeText(this, "The rewarded ad wasn't loaded yet.", Toast.LENGTH_LONG).show()
    }


    companion object {
        private const val TAG = "RewardActivity"
    }
}