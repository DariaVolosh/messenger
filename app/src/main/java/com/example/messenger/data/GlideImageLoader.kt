package com.example.messenger.data

import android.net.Uri
import android.widget.ImageView
import com.bumptech.glide.Glide
import javax.inject.Inject

class GlideImageLoader @Inject constructor(): ImageLoader {
    override fun load(uri: Uri, target: ImageView) {
        Glide.with(target.context)
            .load(uri)
            .into(target)
    }
}