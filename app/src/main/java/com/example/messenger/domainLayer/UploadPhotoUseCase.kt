package com.example.messenger.domainLayer

import android.net.Uri
import com.example.messenger.data.User
import com.example.messenger.dataLayer.ImagesRepository
import javax.inject.Inject

class UploadPhotoUseCase @Inject constructor(
    private val imagesRepository: ImagesRepository
) {
    fun uploadPhoto(photoUri: Uri, user: User) {
        imagesRepository.uploadPhoto(photoUri, user)
    }
}