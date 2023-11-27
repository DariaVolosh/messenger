package com.example.messenger.domain

import android.net.Uri
import android.widget.ImageView
import com.example.messenger.data.ImageLoader
import javax.inject.Inject

class LoadImageUseCase @Inject constructor(
    private val imageLoader: ImageLoader
) {
    fun loadImage(uri: Uri, imageView: ImageView) {
        imageLoader.load(uri, imageView)
    }
}