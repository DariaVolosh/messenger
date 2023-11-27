package com.example.messenger.data

import android.widget.ImageView
import com.bumptech.glide.Glide

class GlideImageLoader: ImageLoader {
    override fun load(uri: String, target: ImageView) {
        Glide.with(target.context)
            .load(uri)
            .into(target)
    }
}