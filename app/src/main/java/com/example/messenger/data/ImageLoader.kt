package com.example.messenger.data

import android.widget.ImageView
interface ImageLoader {
    fun load(url: String, target: ImageView)
}