package com.example.messenger.domain.image

import android.net.Uri
import android.widget.ImageView
import com.example.messenger.data.ImageLoader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class LoadImageUseCase @Inject constructor(
    private val imageLoader: ImageLoader
) {
    suspend fun loadImage(uri: Uri, imageView: ImageView) {
        withContext(Dispatchers.IO) {
            imageLoader.load(uri, imageView)
        }
    }
}