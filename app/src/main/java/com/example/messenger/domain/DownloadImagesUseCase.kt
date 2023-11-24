package com.example.messenger.domain

import android.net.Uri
import com.example.messenger.data.ImagesRepository
import com.example.messenger.data.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class DownloadImagesUseCase @Inject constructor(
    private val imagesRepository: ImagesRepository
) {
    suspend fun getImages(list: List<User>): List<Uri> =
        withContext(Dispatchers.IO) {
            imagesRepository.getImages(list)
        }
}