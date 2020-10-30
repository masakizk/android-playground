package com.example.exoplayer

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.exoplayer.databinding.ActivityMainBinding
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.util.MimeTypes
import com.google.android.exoplayer2.util.Util


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private lateinit var playerView: PlayerView
    private var player: SimpleExoPlayer? = null
    private lateinit var playbackStateListener: PlaybackStateListener

    /**
     *  プレイヤーの状態
     */
    // 再生 or 一時停止
    private var playWhenReady = true
    // 現在のウィンドウインデックス
    private var currentWindow = 0
    // 現在の再生箇所
    private var playbackPosition: Long = 0

    companion object{
        private val TAG: String = MainActivity::class.java.name
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        playerView = binding.videoView

        playbackStateListener = PlaybackStateListener()
    }

    // プレーヤーを初期化する
    override fun onStart() {
        super.onStart()
        if (Util.SDK_INT >= 24) {
            initializePlayer();
        }
    }

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
        // adaptive streamingを実現するために、track selectionを追加
        // どのトラックを取得するかを決める
        val trackSelector = DefaultTrackSelector(this).apply {
            // Sd: Standard-definition television
            setParameters(this.buildUponParameters().setMaxVideoSizeSd())
        }

        if(player == null){
            player = SimpleExoPlayer.Builder(this)
                .setTrackSelector(trackSelector)
                .build()
                .apply {
                    // リスナーを設定
                    addListener(playbackStateListener)
                    prepare()
                }

            playerView.player = player
        }

        // fromUri: ファイル拡張子によってメディアフォーマットを判断する
        val mediaItem = MediaItem.fromUri(getString(R.string.media_url_mp4))

        val soundMediaItem = MediaItem.fromUri(getString(R.string.media_url_mp3))

        // DASHを再生するにはBuilderを使う
        // DASHにはファイル拡張子が存在しないのでMimeTypeを指定する
        val dashMediaItem = MediaItem.Builder()
            .setUri(getString(R.string.media_url_dash))
            .setMimeType(MimeTypes.APPLICATION_MPD)
            .build()


        player?.setMediaItem(mediaItem)
        player?.addMediaItem(soundMediaItem);
        player?.addMediaItem(dashMediaItem);

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
            playWhenReady = player!!.playWhenReady
            playbackPosition = player!!.currentPosition
            currentWindow = player!!.currentWindowIndex

            /**
             * プレイヤーを解放
             */
            player?.removeListener(playbackStateListener)
            player?.release()
            player = null
        }
    }
}