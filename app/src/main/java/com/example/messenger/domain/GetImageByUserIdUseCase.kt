package com.example.messenger.domain

import android.net.Uri
import com.example.messenger.data.ImagesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GetImageByUserIdUseCase @Inject constructor(
    private val imagesRepository: ImagesRepository
) {
    suspend fun getImageById(id: String): Uri = withContext(Dispatchers.IO) {
        imagesRepository.getImageById(id)
    }
}