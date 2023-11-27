package com.example.messenger.data

import android.net.Uri
import android.widget.ImageView
interface ImageLoader {
    fun load(url: Uri, target: ImageView)
}