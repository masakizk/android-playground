package com.example.exoplayer

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.exoplayer.databinding.ActivityMainBinding
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.util.Util


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private lateinit var playerView: PlayerView
    private var player: SimpleExoPlayer? = null

    private var playWhenReady = true
    private var currentWindow = 0
    private var playbackPosition: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        playerView = binding.videoView
    }

    override fun onStart() {
        super.onStart()
        if (Util.SDK_INT >= 24) {
            initializePlayer();
        }
    }

    // プレーヤーを初期化する
    override fun onResume() {
        super.onResume()

        hideSystemUi();
        if ((Util.SDK_INT < 24 || player == null)) {
            initializePlayer();
        }
    }

    // バックグラウンドのときはリソースを開放する
    override fun onPause() {
        super.onPause()
        if (Util.SDK_INT < 24) {
            releasePlayer();
        }
    }

    override fun onStop() {
        super.onStop()
        if (Util.SDK_INT >= 24) {
            releasePlayer();
        }
    }

    private fun initializePlayer() {
        player = SimpleExoPlayer.Builder(this).build()
        playerView.player = player

        val mediaItem = MediaItem.fromUri(getString(R.string.media_url_mp4))
        val secondMediaItem = MediaItem.fromUri(getString(R.string.media_url_mp3))
        player?.setMediaItem(mediaItem)
        player?.addMediaItem(secondMediaItem);

        /**
         * プレイヤーの設定を読み込み
         */
        player?.playWhenReady = playWhenReady
        player?.seekTo(currentWindow, playbackPosition)
        player?.prepare()
    }

    @SuppressLint("InlinedApi")
    private fun hideSystemUi() {
        playerView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LOW_PROFILE
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
    }

    private fun releasePlayer() {
        if (player != null) {
            /**
             * プレイヤーの設定を変数に保存
             */
            // Play/Pause state
            playWhenReady = player!!.playWhenReady
            // current play back position
            playbackPosition = player!!.currentPosition
            // current window index
            currentWindow = player!!.currentWindowIndex
            /**
             * プレイヤーを解放
             */
            player!!.release()
            player = null
        }
    }
}