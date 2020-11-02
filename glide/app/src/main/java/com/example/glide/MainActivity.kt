package com.example.glide

import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.glide.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater).apply {
            setContentView(root)
            downSample.setOnClickListener {
                val intent = Intent(this@MainActivity, DownSampleActivity::class.java)
                startActivity(intent)
            }
            placeholder.setOnClickListener {
                val intent = Intent(this@MainActivity, PlaceholderActivity::class.java)
                startActivity(intent)
            }
        }


        // 画像を表示
        Glide.with(this)
            .load("https://picsum.photos/200/450")
            .into(binding.imageContainer)

        // 画像を丸くする
        Glide.with(this)
            .load("https://picsum.photos/200/200")
            .circleCrop()
            .into(binding.icon)

        // 画像サイズを指定する
        // なしも指定しないと画像をスクリーンサイズで読み込まれる
        // 元画像のサイズのまま読み込んでいないなら
        // - .override(Target.SIZE_ORIGINAL),
        // - 画像サイズにMATCH_PARENTを指定
        // - override(width, height)で固定サイズを指定
        Glide.with(this)
            .load("https://picsum.photos/450/300")
            .listener(listener)
            .override(450, 300)
            .into(binding.fixedSizeImage)
    }

    private val listener = object : RequestListener<Drawable> {
        override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean
        ): Boolean {
            return false
        }

        override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
            val bitmap = (resource as BitmapDrawable).bitmap
            Log.d(TAG, "onResourceReady: bytes: ${bitmap.byteCount}, ${bitmap.width}x${bitmap.height}")
            return false
        }

    }

    companion object {
        private const val TAG = "MainActivity"
    }
}