package com.example.glide

import android.app.Activity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.example.glide.databinding.ActivityPlaceholderBinding

class PlaceholderActivity : Activity() {
    private lateinit var binding: ActivityPlaceholderBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlaceholderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Glide.with(this)
            .load("https://picsum.photos/500/500")
            .placeholder(R.drawable.ic_launcher_background)
            .into(binding.imageOne)

        Glide.with(this)
            .load("https://picsum.photos/500/500")
            .thumbnail(Glide.with(this).load(R.drawable.placeholder_animation))
            .into(binding.imageTwo)

    }
}