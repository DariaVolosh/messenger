package com.example.messenger.domain.image

import android.net.Uri
import com.example.messenger.data.model.User
import com.example.messenger.data.repositories.ImagesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class UploadPhotoUseCase @Inject constructor(
    private val imagesRepository: ImagesRepository
) {
    suspend fun uploadPhoto(photoUri: Uri, user: User): Boolean = withContext(Dispatchers.IO) {
        imagesRepository.uploadPhoto(photoUri, user)
    }
}