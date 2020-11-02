package com.example.glide

import android.app.Activity
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.DownsampleStrategy
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.glide.databinding.ActivityDownSizeSampleBinding

class DownSampleActivity : Activity() {
    private lateinit var binding: ActivityDownSizeSampleBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDownSizeSampleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        downSample(DownsampleStrategy.AT_MOST, binding.atMost)
        downSample(DownsampleStrategy.AT_LEAST, binding.atLeast)
        downSample(DownsampleStrategy.CENTER_INSIDE, binding.centerInside)
        downSample(DownsampleStrategy.CENTER_OUTSIDE, binding.centerOutside)
        downSample(DownsampleStrategy.FIT_CENTER, binding.fitCenter)


    }

    private fun downSample(strategy: DownsampleStrategy, imageView: ImageView) {
        Glide.with(this)
            .load("https://picsum.photos/200/200")
            .listener(listener(strategy))
            .downsample(strategy)
            .dontTransform()
            .into(imageView)
    }

    private fun listener(strategy: DownsampleStrategy) = object : RequestListener<Drawable> {
        override fun onLoadFailed(
            e: GlideException?,
            model: Any?,
            target: Target<Drawable>?,
            isFirstResource: Boolean
        ): Boolean {
            return false
        }

        override fun onResourceReady(
            resource: Drawable?,
            model: Any?,
            target: Target<Drawable>?,
            dataSource: DataSource?,
            isFirstResource: Boolean
        ): Boolean {
            val bitmap = (resource as BitmapDrawable).bitmap
            Log.d(
                TAG,
                "${strategy::class.java.name}: bytes: ${bitmap.byteCount}, ${bitmap.width}x${bitmap.height}"
            )
            return false
        }
    }

    companion object {
        private const val TAG = "DownSampleActivity"
    }

}